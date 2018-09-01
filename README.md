# GambleBot - AceOnline lab bruteforce-gambling

[![Software License][ico-license]](LICENSE.md)

This program can use the laboratory in AceOnline like games and put specified fixes on your items.
It is especially useful on private servers, where gamble-cards are free, but certain fixes really rare and require a huge amount of tries.

## Features

* Auto-gamble weapons and armor
* Specify a list of acceptable pre- & suffixes
* Automatically build the next weapon once one if finished, useful if you need many for Legend-upgrade
* Resources (fixes, items to use) customizable by you (image files)
* Break the loop by hitting "ESC" (also closes lab)
* Randomized delays to make detection harder

## Build

``` bash
$ mvn clean compile assembly:single
```

## Download

Check the [Releases][link-releases] section for compiled versions.

## Usage

* Put your desired pre- & suffixes in *config.txt*
* Copy or link the gambles, resets, *enchant.png* and identifier item you are using
* Copy or link the fixes you specified
* Set up your [ingame inventory](#inventory)
``` bash
$ java -jar GambleBot.jar
```
* Run the program (*cmd*) as admin if the Robot (mouse movement) doesn't work

## Config

You can create a file `config`[`.properties`](https://en.wikipedia.org/wiki/.properties#Format) with these values:

* factordelay (default: 400): MS to wait between the two factor button clicks
* showdelay (default: 1250): MS to hover the weapon between gamble attempts
* numberW (default: 1): It's possible to auto-gamble a bunch of items at once

## Inventory

![Example Inventory](/res/example_inv.png)

* 1: Identifier
* 54-56: Weapons to gamble
* It does not matter where the gambles and resets are, as long you linked/copied the correct images
* There can be more items in your inventory than 60, it only matters what you see when fully scrolled down. Sometimes, if the last row is filled completely, a new line will appear and disappear based on whether the item is currently in lab; this can cause issues, so you should avoid filling the last line up completely.
* The items to gamble must not have only 1 fix and must at least be Enchant-1

## Resources

Fixes and positions are scanned by comparing pixel-wise against the provided image files. Even the slightest difference in color matters.
This means, on certain servers you may have to create your own files. Black (`#000000`) pixels are ignored, so fill up anything but the text with.

## Credits

- [All Contributors][link-contributors]

## License

The MIT License (MIT). Please see the [License File](LICENSE.md) for more information.

[ico-license]: https://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat-square

[link-releases]: https://github.com/DrDelay/GambleBot/releases
[link-contributors]: ../../contributors
