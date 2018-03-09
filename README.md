# android-smile-login
A simple android app that use user's smiling image to login

The app use Google Mobile Vision API (https://developers.google.com/vision/) to have the face detection and get the smiling probability. 

User first asked to take a picture of their face and the app will determine whether the user have smiling enough to login. 

The login will not successful if there is no face object detected and the user not smiling enough. Only one face object that will be analyzed if there are multiple faces in the image taken.