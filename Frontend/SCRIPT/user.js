//the module which will store the user info required for api calls in the backend

// user.js
export const user = {
    id:0,
    phoneNo: 0
};

export function updateUser(data) {
    user.id = data.id;
    user.phoneNo = data.phoneNumber;
    localStorage.setItem('currentUser', JSON.stringify({id: user.id, phoneNo: user.phoneNo}));
}

export function getUser() {
    // Try to get from localStorage if not set yet
    if (user.id === 0) {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            const userData = JSON.parse(storedUser);
            user.id = userData.id;
            user.phoneNo = userData.phoneNo;
        }
    }
    return user;
}