import { createStore } from "vuex";

export default createStore({
    state: {
        currentUser: {
            uid: null,
            userAccount: null,
            username: null,
            userAvatar: null,
            userAge: null,
            userGender: null,
            userTags: null,
            GitHub: null,
            personalWeb: null,
            csdn: null,
            userEmail: null,
            userPhone: null
        },
    },
    mutations: {
        setUid(state, uid) {
            state.currentUser.uid = uid;
        },
        setUserAccount(state, userAccount) {
            state.currentUser.userAccount = userAccount;
        },
        setUsername(state, username) {
            state.currentUser.username = username;
        },
        setUserAvatar(state, userAvatar) {
            state.currentUser.userAvatar = userAvatar;
        },
        setUserAge(state, userAge) {
            state.currentUser.userAge = userAge;
        },
        setUserGender(state, userGender) {
            state.currentUser.userGender = userGender;
        },
        setUserTags(state, userTags) {
            state.currentUser.userTags = userTags;
        },
        setGitHub(state, gitHub) {
            state.currentUser.GitHub = gitHub;
        },
        setPersonalWeb(state, personalWeb) {
            state.currentUser.personalWeb = personalWeb;
        },
        setCsdn(state, csdn) {
            state.currentUser.csdn = csdn;
        },
        setUserEmail(state, userEmail) {
            state.currentUser.userEmail = userEmail;
        },
        setUserPhone(state, userPhone) {
            state.currentUser.userPhone = userPhone;
        },
    },
});