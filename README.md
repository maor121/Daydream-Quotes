Daydream Quotes Motion Aware
============================

Android app to display random Quotes from the internet on Android TV. 
If camera is connected, the app will check every minute if there is motion in the room. And sleep if no motion is detected.

Quote sources
=============

The app uses various quote apis to get new quotes all the time:

0. http://quotesondesign.com/api-v4-0/
1. http://forismatic.com/en/api/

More can be added by recompiling the project.

Motion detection
================

The app uses OpenCV (open source computer vision library) to detect motion in the room.
The camera will sleep for 1 minute when motion is detected (you don't want to film your room 24\7, probably).

If you want camera to sleep for more time, edit the constants in DreamService.java
