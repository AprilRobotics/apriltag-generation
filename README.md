AprilTag-Generation
===================

Generate AprilTags in custom layouts for [AprilTag 3](https://github.com/AprilRobotics/apriltag-generation).

Example usage for generating the 21h7 circular tag family:
```
$ ant
$ java -cp april.jar april.tag.TagFamilyGenerator circle_9 7
```
Then to generate the c code and tag images after copying the output into the source folder:
```
$ ant
$ java -cp april.jar april.tag.TagToC april.tag.Tag21h7
$ java -cp april.jar april.tag.GenerateTags april.tag.TagStandard21h7 .
```
