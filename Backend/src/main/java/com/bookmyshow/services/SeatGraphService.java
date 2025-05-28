package com.bookmyshow.services;

import org.springframework.stereotype.Service;

import com.bookmyshow.models.Seat;
import com.bookmyshow.models.Show;
import com.bookmyshow.repositories.SeatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.IntStream;

//@Service
//public class SeatGraphService {
//    @Autowired
//    private SeatRepository seatRepository;
//
//    /**
//     * Finds a group of adjacent available seats for a given show.
//     *
//     * @param showId    The ID of the show.
//     * @param numSeats  The number of adjacent seats to find.
//     * @return A list of adjacent Seat objects, or an empty list if no such group is found.
//     *
//     * Logic:
//     * 1. Fetches all available (not booked) seats for the given show from the database.
//     * 2. Sorts the retrieved seats first by row number and then by seat number within the row.
//     * This ensures that seats are ordered in a way that allows us to check for adjacency.
//     * 3. Iterates through the sorted list, creating sublists ("groups") of the required size (numSeats).
//     * 4. For each group, it calls the isAdjacent() method to check if the seats are adjacent.
//     * 5. If a group of adjacent seats is found, it returns that group.
//     * 6. If no adjacent group is found after checking all possible groups, it returns an empty list.
//     */
//    public List<Seat> findAdjacentSeats(int showId, int numSeats) {
//        List<Seat> availableSeats = seatRepository.findByShowIdAndIsBookedFalse(showId);
//        availableSeats.sort(Comparator.comparing(Seat::getRowNumber).thenComparing(Seat::getSeatNumberInRow));
//
//        for (int i = 0; i <= availableSeats.size() - numSeats; i++) {
//            List<Seat> group = availableSeats.subList(i, i + numSeats);
//            if (isAdjacent(group)) {
//                return group;
//            }
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * Checks if a list of seats are adjacent.
//     *
//     * @param group The list of Seat objects to check.
//     * @return true if the seats are adjacent, false otherwise.
//     *
//     * Logic:
//     * 1. If the group contains only one seat, it's considered "adjacent" (true).
//     * 2. Checks if all seats in the group are in the same row.
//     * 3. Checks if the seat numbers within the row are consecutive.
//     * 4. Returns true if both conditions are met, false otherwise.
//     */
//    private boolean isAdjacent(List<Seat> group) {
//        if (group.size() < 2) {
//            return true; // A single seat is always "adjacent"
//        }
//
//        int row = group.get(0).getRowNumber();
//        int firstSeatNumber = group.get(0).getSeatNumberInRow();
//
//        return group.stream().allMatch(seat -> seat.getRowNumber() == row) &&
//                IntStream.range(0, group.size() - 1).allMatch(j -> group.get(j + 1).getSeatNumberInRow() == firstSeatNumber + j + 1);
//    }
//
//    public boolean bookSeats(List<Long> seatIds) {
//        List<Seat> seats = seatRepository.findAllById(seatIds);
//        for (Seat seat : seats) {
//            if (seat.isBooked()) return false; // Seat already booked
//            seat.bookSeat();
//        }
//        seatRepository.saveAll(seats);
//        return true;
//    }
//    
//    
//}

@Service
public class SeatGraphService {
    
    @Autowired
    private SeatRepository seatRepository;
    
    private Map<Long, List<Seat>> seatGraph = new HashMap<>(); // Graph for each showId

    /**
     * Builds a graph representation of the seat arrangement for a given show.
     */
    private void buildSeatGraph(int showId) {
        List<Seat> availableSeats = seatRepository.findByShowIdAndIsBookedFalse(showId);
        seatGraph.put((long) showId, new ArrayList<>(availableSeats));

        Map<String, Seat> seatMap = new HashMap<>();
        for (Seat seat : availableSeats) {
            String key = seat.getRowNumber() + "-" + seat.getSeatNumberInRow();
            seatMap.put(key, seat);
        }

        for (Seat seat : availableSeats) {
            List<Seat> adjacentSeats = new ArrayList<>();
            String nextSeatKey = seat.getRowNumber() + "-" + (seat.getSeatNumberInRow() + 1);

            if (seatMap.containsKey(nextSeatKey)) {
                adjacentSeats.add(seatMap.get(nextSeatKey));
            }

            seatGraph.get((long) showId).add(seat);
        }
    }

    /**
     * Finds a group of adjacent available seats for a given show.
     */
    public List<Seat> findAdjacentSeats(int showId, int numSeats) {
        List<Seat> availableSeats = seatRepository.findByShowIdAndIsBookedFalse(showId); // Fetch fresh data
        Set<Seat> visited = new HashSet<>();

        for (Seat seat : availableSeats) {
            if (!visited.contains(seat)) {
                List<Seat> group = bfsFindAdjacent(seat, numSeats, visited, availableSeats);
                if (!group.isEmpty()) {
                    return group;
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Seat> bfsFindAdjacent(Seat start, int numSeats, Set<Seat> visited, List<Seat> availableSeats) {
        Queue<List<Seat>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));
        visited.add(start);

        while (!queue.isEmpty()) {
            List<Seat> group = queue.poll();
            if (group.size() == numSeats) {
                return group;
            }

            Seat lastSeat = group.get(group.size() - 1);
            for (Seat neighbor : availableSeats) { // Use fresh seat data
                if (!visited.contains(neighbor) && isAdjacent(lastSeat, neighbor)) {
                    List<Seat> newGroup = new ArrayList<>(group);
                    newGroup.add(neighbor);
                    queue.add(newGroup);
                    visited.add(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Checks if two seats are adjacent.
     */
    private boolean isAdjacent(Seat seat1, Seat seat2) {
        return seat1.getRowNumber() == seat2.getRowNumber() &&
               Math.abs(seat1.getSeatNumberInRow() - seat2.getSeatNumberInRow()) == 1;
    }

    /**
     * Books the given list of seats if available.
     */
    public boolean bookSeats(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        for (Seat seat : seats) {
            if (seat.isBooked()) return false;
            seat.bookSeat();
        }
        seatRepository.saveAll(seats);
        return true;
    }
}

