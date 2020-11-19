import {gql} from "apollo-boost"

// //get profile
export const GET_USER_PROFILE = gql `
query ($loggedInUser:String!, $toFollowUser:String!) {
    getUserProfile(input:{loggedInUser:$loggedInUser toFollowUser:$toFollowUser}){
        username
        basicInfo{
            firstName
            lastName
            city
            country
            bioDescription
        }
        numPosts
        numFollowers
        numFollowing
        posts{
            description
            createdAt
            byUser {
                username
                firstName
                lastName
            }
            numLikes
            numComments
            comments{
                commentText
                createdAt
                byUser{
                    username
                    firstName
                    lastName
                }
            }
            likedByUsers{
                username
                firstName
                lastName
            }
        }
        
        followers {
            username
            firstName
            lastName
            city
            country
        }
        following{
            username
            firstName
            lastName
            city
            country
        }
    }
}
`

// //report profile
// export const REPORT_USER_PROFILE = gql `
// mutation ($reportedUser:String!, $reportedBy:String!){
//     reportUser(input:{
//         username:$reportedUser
//         reportedBy:$reportedBy
//     })
// }`

// // edit profile
// export const UPDATE_USER_INFO = gql `mutation {
//     updateUser(input:{
//         firstName:"Chintamani"
//         lastName:"Satavalekar"
//         username:"maitreyastark"
//         email:"koibhiemail@de.com"
//         password:"myPassword"
//         bioDescription:"Curious to cook"
//         city:"Mumbai"
//         country:"India"
//     }) {
//         firstName
//         lastName
//         bioDescription
//         username
//         city
//         country
//     }
// }`


// //get all posts of userprofile
// export const GET_ALL_POSTS_FOR_USER = gql `
// query ($username:String!) {
//     getPosts(username:$username) {
//         description
//         createdAt
//         byUser {
//             username
//             firstName
//             lastName
//         }
//         numLikes
//         numComments
//         comments{
//             commentText
//             createdAt
//             byUser{
//                 username
//                 firstName
//                 lastName
//             }
//         }
//         likedByUsers{
//             username
//             firstName
//             lastName
//         }
//     }
// }`

