# GambleBot - AceOnline lab bruteforce-gambling

[![Software License][ico-license]](LICENSE.md)
[![Latest Stable Version][ico-githubversion]][link-releases]

This program can use the laboratory in AceOnline like games and put specified fixes on your items.
It is especially useful on private servers, where gamble-cards are free, but certain fixes really rare and require a huge amount of tries.

## Features

* Auto-gamble weapons and armor
* Specify a list of acceptable pre- & suffixes
* Automatically build the next weapon once one if finished, useful if you need many for Legend-upgrade
* Ressources completely customizeable by you

## Build

``` bash
$ javac -d ./build src/de/drdelay/gamblebot/*.java
$ cd build
$ jar cfe GambleBot.jar de.drdelay.gamblebot.GambleBot *
```

## Usage

* Put your desired pre- & suffixes in *config.txt*
* Copy or link the gambles, resets, *enchant.png* and identifier item you are using
* Copy or link the fixes you specified
* Set up your [ingame inventory](#inventory)
``` bash
$ cp config.dist.txt config.txt
$ java -jar GambleBot.jar [factordelay showdelay numberOfWeaps]
```

* factordelay (default: 1500): MS to wait between the two factor button clicks
* showdelay (default: 500): MS to hover the weapon between gamble attempts
* numberOfWeaps (default: 1): It's possible to auto-gamble a bunch of items at once

## Inventory

![Example Inventory](/res/example_inv.png)

* 1: Identifier
* 54-56: Weapons to gamble
* It does not matter where the gambles and resets are, aslong you linked/copied the correct images

## Ressources

Fixes and positions are scanned by comparing pixelwise against the provided image files. Even the slightest difference in color matters.
This means, on certain servers you may have to create your own files. Black (#000000) pixels are ignored, so fill up anything but the text with.

## Credits

- [All Contributors][link-contributors]

## License

The MIT License (MIT). Please see the [License File](LICENSE.md) for more information.

[ico-license]: https://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat-square
[ico-githubversion]: https://poser.pugx.org/DrDelay/GambleBot/v/stable

[link-releases]: https://github.com/DrDelay/GambleBot/releases
[link-contributors]: ../../contributors
