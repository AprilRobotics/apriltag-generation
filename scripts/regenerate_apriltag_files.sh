#!/bin/bash
set -e

ant
# Names (without first character, t/T).
NAMES=(ag16h5 ag25h9 ag36h11 agCircle21h7 agCircle49h12 agCustom48h12 agStandard41h12 agStandard52h13)
for name in ${NAMES[*]}; do
    java -cp april.jar april.tag.TagToC april.tag.T$name

    cat scripts/copyright.txt > ../apriltag/t$name.c
    cat t$name.c >> ../apriltag/t$name.c
    rm t$name.c

    cat scripts/copyright.txt > ../apriltag/t$name.h
    cat t$name.h >> ../apriltag/t$name.h
    rm t$name.h
done
