<?php

// Generates fix image files from a screenshot
// Usage: php fixextractor.php filename.bmp

class Point
{
    public function __construct($x, $y)
    {
        $this->x = $x;
        $this->y = $y;
    }

    public $x;
    public $y;

    public function distanceTo(Point $b)
    {
        return abs(sqrt(pow($b->x - $this->x, 2) + pow($b->y - $this->y, 2)));
    }

    public function __toString()
    {
        return sprintf('%d/%d', $this->x, $this->y);
    }
}

function maxPoints(array $points)
{
    $leastLeft = null;
    $mostLeft = null;
    $leastTop = null;
    $mostTop = null;

    foreach ($points as $point) {
        $x = $point->x;
        $y = $point->y;

        if ($x < $leastLeft || $leastLeft === null) {
            $leastLeft = $x;
        }
        if ($x > $mostLeft || $mostLeft === null) {
            $mostLeft = $x;
        }
        if ($y < $leastTop || $leastTop === null) {
            $leastTop = $y;
        }
        if ($y > $mostTop || $mostTop === null) {
            $mostTop = $y;
        }
    }

    return [
        'x1' => $leastLeft,
        'y1' => $leastTop,
        'x2' => $mostLeft,
        'y2' => $mostTop,
    ];
}

$name = $argv[1];
$filename = pathinfo($name, PATHINFO_FILENAME);

$img = imagecreatefrombmp($name);

$size = getimagesize($name);
$width = $size[0];
$height = $size[1];

$greenPos = [];

$black = imagecolorallocate($img, 0, 0, 0);

// Alle grünen Pixel sammeln
for ($x = 0; $x < $width; ++$x) {
    for ($y = 0; $y < $height; ++$y) {
        $rgb = imagecolorat($img, $x, $y);
        $r = ($rgb >> 16) & 0xFF;
        $g = ($rgb >> 8) & 0xFF;
        $b = $rgb & 0xFF;

        if ($r === 0 && $g === 255 && $b === 0) {
            $greenPos[] = new Point($x, $y);
        } else {
            imagesetpixel($img, $x, $y, $black);
        }
    }
}

const MAX_DIST = 50;

$clusters = [];

foreach ($greenPos as $point) {
    $foundClusterIdx = null;

    // Passendes existentes Cluster suchen
    foreach ($clusters as $idx => $cluster) {
        foreach ($cluster as $cpoint) {
            if ($cpoint->distanceTo($point) <= MAX_DIST) {
                $foundClusterIdx = $idx;
                break 2;
            }
        }
    }

    if ($foundClusterIdx !== null) {
        $clusters[$foundClusterIdx][] = $point;
    } else {
        $clusters[] = [$point];
    }
}

const MIN_IN_CLUSTER = 50;

$relevantClusters = array_filter($clusters, function ($cluster) {
    return sizeof($cluster) > MIN_IN_CLUSTER;
});

// Boundaries
$imgs = [];
foreach ($relevantClusters as $cluster) {
    $boundaries = maxPoints($cluster);
    $crop = [
        'x' => $boundaries['x1'] - 1,
        'y' => $boundaries['y1'] - 1,
        'width' => ($boundaries['x2'] - $boundaries['x1']) + 3,
        'height' => ($boundaries['y2'] - $boundaries['y1']) + 3,
    ];
    print_r($crop);
    $imgs[] = imagecrop($img, $crop);
}

// Save
foreach ($imgs as $i => $newImg) {
    imagepng($newImg, $filename . '_' . $i . '.png');
}
