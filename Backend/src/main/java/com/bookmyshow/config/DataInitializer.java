package com.bookmyshow.config;

import com.bookmyshow.constants.Constants;
import com.bookmyshow.models.*;
import com.bookmyshow.repositories.*;
import com.bookmyshow.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component("dataInitializer")
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PricingTierRepository pricingTierRepository;

    @Autowired
    private TMDBService tmdbService;

    @Value("${tmdb.poster.path}")
    private String tmdbPosterPath;

    public DataInitializer(MovieRepository movieRepository, TheaterRepository theaterRepository,
                           ShowRepository showRepository,
                           BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            loadActualData();
            movieService.updateMoviesFastSearch();
            theaterService.updateTheatreFastSearch();
        } catch (Exception e) {
            System.err.println("⚠ Failed to fetch actual movie data. Loading sample data instead...");
            loadSampleData();
        }
    }

    private void loadActualData() {
    	// Insert Movies
        List<Movie> nowPlayingMovies = fetchNowPlayingMovies();
        movieRepository.saveAll(nowPlayingMovies);

        // Insert Theaters
        List<Theater> theatreList = new ArrayList<>();
        theatreList.add(new Theater("PVR Cinemas", "Mumbai"));
        theatreList.add(new Theater("INOX", "Delhi"));
        theaterRepository.saveAll(theatreList);

        //Insert Shows
        List<Show> showList = createShows(nowPlayingMovies, theatreList);
        showRepository.saveAll(showList);

        // Insert Pricing
        List<Pricing> pricingList = new ArrayList<>();
        showList.stream().forEach(show -> {
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_LUXURY));
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_PREMIUM));
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_ECONOMY));
        });
        pricingRepository.saveAll(pricingList);

        // Insert Seats
        int rows = 5, cols = 20;
        List<Seat> seats = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                for (Show show : showList) {
                	boolean isBooked = false;
                	if((r==1 && c%2==0) || (r==2 && c%3==0) || (r==3 && c%4==0) || (r==4 && c%5==0) || (r==5 && c%6==0))
                		isBooked = true;
                	else
                		isBooked = false;
//                	if(c != 1)
//                		isBooked = true;
//                	else
//                		isBooked = false;
                	Seat seat = new Seat(r, c, show, isBooked);
                    if (r == 1) {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_LUXURY);
                    } else if (r >= 2 && r <= 3) {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_PREMIUM);
                    } else {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_ECONOMY);
                    }
                    seats.add(seat);
                }
            }
        }
        seats = seatRepository.saveAll(seats);

        // Insert Users
        List<User> userList = new ArrayList<>();
        User user1 = new User("Rupam", "rupam@gmail.com", 8011263116L, "Test@1");
        userList.add(userService.createUser(user1));
        User user2 = new User("Krish", "krish@gmail.com", 9820605768L, "Test@2");
        userList.add(userService.createUser(user2));

        // Insert Pricing Tier
        buildPricingTier();

        // Insert Bookings
        Seat seat1 = seats.get(0);
        Seat seat2 = seats.get(1);
        Seat seat3 = seats.get(2);
        Seat seat4 = seats.get(3);
        Seat seat5 = seats.get(4);
        List<Seat> seatinput1 = new ArrayList<>();
        seatinput1.add(seat1);
        seatinput1.add(seat2);
        seatinput1.add(seat3);
        List<Seat> seatinput2 = new ArrayList<>();
        seatinput2.add(seat4);
        seatinput2.add(seat5);
        Booking booking1 = new Booking(user1, showList.get(0), seatinput1);
        bookingService.bookSeats(booking1.getShow().getId(), booking1.getSeats().stream().map(Seat::getId).collect(Collectors.toList()), userList.get(0).getId());
        Booking booking2 = new Booking(user2, showList.get(1), seatinput2);
        bookingService.bookSeats(booking2.getShow().getId(), booking2.getSeats().stream().map(Seat::getId).collect(Collectors.toList()), userList.get(1).getId());

        System.out.println("✅ Sample data initialized successfully!");
    }

    private void buildPricingTier() {
        List<PricingTier> list = new ArrayList<>();
        list.add(new PricingTier(new BigDecimal("1.0"), 100, Duration.ofHours(24)));
        list.add(new PricingTier(new BigDecimal("1.2"), 50, Duration.ofHours(12)));
        list.add(new PricingTier(new BigDecimal("1.5"), 20, Duration.ofHours(6)));
        list.add(new PricingTier(new BigDecimal("1.8"), 10, Duration.ofHours(2)));
        pricingTierRepository.saveAll(list);
    }

    private static Pricing buildPricing(Show show, String seatCategory) {
        Pricing pricing = new Pricing();
        pricing.setShowId(show.getId());
        pricing.setSeatCategory(seatCategory);
        if(seatCategory=="SEAT_CATEGORY_ECONOMY")
        	pricing.setBasePrice(BigDecimal.valueOf(500));
        else if(seatCategory=="SEAT_CATEGORY_PREMIUM")
        	pricing.setBasePrice(BigDecimal.valueOf(600));
        else
        	pricing.setBasePrice(BigDecimal.valueOf(750));
        return pricing;
    }

    private List<Show> createShows(List<Movie> nowPlayingMovies, List<Theater> theatreList) {
        List<Show> showList = new ArrayList<>();
        
        for (Theater theatre : theatreList) {
            LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(8, 30)).plusDays(1);

            for (Movie movie : nowPlayingMovies) {  
                Show show = new Show();
                show.setMovie(movie);
                show.setTheater(theatre);
                show.setShowTime(startTime);
                showList.add(show);  
                
                startTime = adjustStartTime(startTime);
            }
        }

        return showList;
    }

    private LocalDateTime adjustStartTime(LocalDateTime startTime) {
        LocalTime startTimeLimit = LocalTime.of(8, 30, 0);
        LocalTime endTimeLimit = LocalTime.of(23, 30, 0);

        LocalDateTime nextTime = startTime.plusHours(3);
        
        if (nextTime.toLocalTime().isAfter(endTimeLimit)) {
            nextTime = nextTime.plusDays(1).with(startTimeLimit);
        } 

        else if (nextTime.toLocalTime().isBefore(startTimeLimit)) {
            nextTime = nextTime.with(startTimeLimit);
        }

        return nextTime;
    }

    private void loadSampleData() {
        // Insert Movies
        Movie movie1 = new Movie("Snow White", "Family, Fantasy", 180, 2.0, "Princess Snow White flees the castle when the Evil Queen, in her jealousy over Snow White's inner beauty, tries to kill her. Deep into the dark woods, she stumbles upon seven magical dwarves and a young thief named Jonathan. Together, they strive to survive the Queen's relentless pursuit and aspire to take back the kingdom in the process...", "https://image.tmdb.org/t/p/original/xWWg47tTfparvjK0WJNX4xL8lW2.jpg");
        Movie movie2 = new Movie("Sikandar", "Action, Thriller", 180, 3.0, "Sanjay \"Sikandar\" Rajkot, the king of Rajkot, becomes a target for Minister Rakesh Pradhan after he gets in an altercation with his son.", "https://image.tmdb.org/t/p/original/41s42CRXafa3OuRGvCtfYPEBmse.jpg");
        Movie movie3 = new Movie("Mickey 17", "Science Fiction, Comedy, Adventure", 180, 3.5, "Unlikely hero Mickey Barnes finds himself in the extraordinary circumstance of working for an employer who demands the ultimate commitment to the job… to die, for a living.", "https://image.tmdb.org/t/p/original/edKpE9B5qN3e559OuMCLZdW1iBZ.jpg");
        Movie movie4 = new Movie("Black Bag", "Drama, Thriller, Mystery", 180, 3.5, "When his beloved wife is suspected of betraying the nation, an intelligence agent faces the ultimate test – loyalty to his marriage or his country.", "https://image.tmdb.org/t/p/original/hHPovtU4b96LHcoeEwRkGHI5btw.jpg");
        Movie movie5 = new Movie("The Diplomat", "Thriller, Drama", 180, 3.5, "The Deputy High Commissioner, J.P. Singh, faces an unusual crisis when a mysterious woman rushes inside the Indian High Commission in Islamabad, claiming to be an Indian citizen and seeking a return to India.", "https://image.tmdb.org/t/p/original/uwl9Y4px8TTIQK4N19k6gnW1RnX.jpg");
        List<Movie> nowPlayingMovies = new ArrayList<>();
        nowPlayingMovies.add(movie1);
        nowPlayingMovies.add(movie2);
        nowPlayingMovies.add(movie3);
        nowPlayingMovies.add(movie4);
        nowPlayingMovies.add(movie5);
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4, movie5));

//        // Insert Theaters
//        Theater theater1 = new Theater("PVR Cinemas", "Mumbai");
//        Theater theater2 = new Theater("INOX", "Delhi");
//        theaterRepository.saveAll(Arrays.asList(theater1, theater2));
//
//        // Insert Shows
//        int rows = 5, cols = 5;
//        List<Seat> seats1 = new ArrayList<>();
//        List<Seat> seats2 = new ArrayList<>();
//
//        // Creating Shows with seats
//        Show show1 = new Show(movie1, theater1, LocalDateTime.of(2025, 3, 16, 18, 0));
//        Show show2 = new Show(movie2, theater2, LocalDateTime.of(2025, 3, 17, 20, 30));
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                Seat seat1 = new Seat(r, c, show1);
//                seats1.add(seat1);
//                Seat seat2 = new Seat(r, c, show2);
//                seats2.add(seat2);
//            }
//        }
//
//        showRepository.saveAll(Arrays.asList(show1, show2));
//        seatRepository.saveAll(seats1);
//        seatRepository.saveAll(seats2);
//
//        // Insert Users
//        List<User> userList = new ArrayList<>();
//        User user1 = new User("John Doe", "john@example.com", 9867226421L, "Test1");
//        userList.add(userService.createUser(user1));
//        User user2 = new User("Jane Doe", "jane@example.com", 9820605768L, "Test2");
//        userList.add(userService.createUser(user2));
//
//        Seat seat1 = seats1.get(0);
//        Seat seat2 = seats1.get(1);
//        Seat seat3 = seats1.get(2);
//        Seat seat4 = seats2.get(0);
//        Seat seat5 = seats2.get(1);
//        List<Seat> seatinput1 = new ArrayList<>();
//        seatinput1.add(seat1);
//        seatinput1.add(seat2);
//        seatinput1.add(seat3);
//        List<Seat> seatinput2 = new ArrayList<>();
//        seatinput2.add(seat4);
//        seatinput2.add(seat5);
//
//        // Insert Bookings
//        Booking booking1 = new Booking(user1, show1, seatinput1);
//        Booking booking2 = new Booking(user2, show2, seatinput2);
//        bookingRepository.saveAll(Arrays.asList(booking1, booking2));
//
//        System.out.println("✅ Sample data initialized successfully!");
        
     // Insert Theaters
        List<Theater> theatreList = new ArrayList<>();
        theatreList.add(new Theater("PVR Cinemas", "Mumbai"));
        theatreList.add(new Theater("INOX", "Delhi"));
        theaterRepository.saveAll(theatreList);

        //Insert Shows
        List<Show> showList = createShows(nowPlayingMovies, theatreList);
        showRepository.saveAll(showList);

        // Insert Pricing
        List<Pricing> pricingList = new ArrayList<>();
        showList.stream().forEach(show -> {
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_LUXURY));
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_PREMIUM));
            pricingList.add(buildPricing(show, Constants.SEAT_CATEGORY_ECONOMY));
        });
        pricingRepository.saveAll(pricingList);

        // Insert Seats
        int rows = 5, cols = 20;
        List<Seat> seats = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                for (Show show : showList) {
                	boolean isBooked = false;
                	if((r==1 && c%2==0) || (r==2 && c%3==0) || (r==3 && c%4==0) || (r==4 && c%5==0) || (r==5 && c%6==0))
                		isBooked = true;
                	else
                		isBooked = false;
                	Seat seat = new Seat(r, c, show, isBooked);
                    if (r == 1 || r == 2) {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_LUXURY);
                    } else if (r > 2 && r <= 4) {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_PREMIUM);
                    } else {
                        seat.setSeatCategory(Constants.SEAT_CATEGORY_ECONOMY);
                    }
                    seats.add(seat);
                }
            }
        }
        seats = seatRepository.saveAll(seats);

        // Insert Users
        List<User> userList = new ArrayList<>();
        User user1 = new User("Rupam", "rupam@gmail.com", 8011263116L, "Test@1");
        userList.add(userService.createUser(user1));
        User user2 = new User("Krish", "krish@gmail.com", 9820605768L, "Test@2");
        userList.add(userService.createUser(user2));

        // Insert Pricing Tier
        buildPricingTier();

        // Insert Bookings
        Seat seat1 = seats.get(0);
        Seat seat2 = seats.get(1);
        Seat seat3 = seats.get(2);
        Seat seat4 = seats.get(3);
        Seat seat5 = seats.get(4);
        List<Seat> seatinput1 = new ArrayList<>();
        seatinput1.add(seat1);
        seatinput1.add(seat2);
        seatinput1.add(seat3);
        List<Seat> seatinput2 = new ArrayList<>();
        seatinput2.add(seat4);
        seatinput2.add(seat5);
        Booking booking1 = new Booking(user1, showList.get(0), seatinput1);
        bookingService.bookSeats(booking1.getShow().getId(), booking1.getSeats().stream().map(Seat::getId).collect(Collectors.toList()), userList.get(0).getId());
        Booking booking2 = new Booking(user2, showList.get(1), seatinput2);
        bookingService.bookSeats(booking2.getShow().getId(), booking2.getSeats().stream().map(Seat::getId).collect(Collectors.toList()), userList.get(1).getId());

        System.out.println("✅ Sample data initialized successfully!");
        
    }

    private List<Movie> fetchNowPlayingMovies() {
        ResponseEntity<Map> response = tmdbService.getNowPlayingMovies("In", "Hindi");
        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            if (!CollectionUtils.isEmpty(results)) {
                return results.stream().map(m -> {
                    Movie movie = new Movie();
                    movie.setName((String) m.get("title"));
                    movie.setOverview((String) m.get("overview"));
                    movie.setPosterPath(tmdbPosterPath + m.get("poster_path"));

                    // Convert genre IDs to names
                    List<Integer> genreIds = (List<Integer>) m.get("genre_ids");
                    List<String> genreNames = genreIds.stream()
                        .map(id -> GENRE_MAP.getOrDefault(id, "Unknown"))
                        .collect(Collectors.toList());
                    movie.setGenre(String.join(", ", genreNames));

                    // Set default duration
                    movie.setDuration(180);

                    // Extract, scale rating to 5, and round to nearest 0.5
                    double ratingOutOf10 = m.get("vote_average") != null ? ((Number) m.get("vote_average")).doubleValue() : 0.0;
                    double ratingOutOf5 = (ratingOutOf10 / 2.0);
                    double roundedRating = Math.round(ratingOutOf5 * 2) / 2.0;
                    movie.setRating(roundedRating);

                    return movie;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }


    
    private static final Map<Integer, String> GENRE_MAP = Map.ofEntries(
    	    Map.entry(28, "Action"),
    	    Map.entry(12, "Adventure"),
    	    Map.entry(16, "Animation"),
    	    Map.entry(35, "Comedy"),
    	    Map.entry(80, "Crime"),
    	    Map.entry(99, "Documentary"),
    	    Map.entry(18, "Drama"),
    	    Map.entry(10751, "Family"),
    	    Map.entry(14, "Fantasy"),
    	    Map.entry(36, "History"),
    	    Map.entry(27, "Horror"),
    	    Map.entry(10402, "Music"),
    	    Map.entry(9648, "Mystery"),
    	    Map.entry(10749, "Romance"),
    	    Map.entry(878, "Science Fiction"),
    	    Map.entry(10770, "TV Movie"),
    	    Map.entry(53, "Thriller"),
    	    Map.entry(10752, "War"),
    	    Map.entry(37, "Western")
    	);

}