Spoutcraft Launcher
===================
Spoutcraft Launcher is the custom launcher for installing and updating Spoutcraft with built-in byte patching for easy upgrading/downgrading of Minecraft versions.

Like the project? Feel free to [donate] to help continue development!

## What is Spoutcraft?
Spoutcraft is a modified version of the Minecraft client, which you can access through the the Spoutcraft Launcher, and when connecting to a server with SpoutPlugin, it allows you to unlock most of the features of SpoutPlugin, such as custom blocks, items, texture packs, etc. It also gives several speed improvements, a minimap, an overview map, etc.

[Homepage] | [Forums] | [Twitter] | [Facebook]

## The License
Spoutcraft Launcher is licensed under the [GNU Lesser General Public License Version 3][License], but with a provision that files are released under the MIT license 180 days after they are published. Please see the `LICENSE.txt` file for details.

Copyright (c) 2011-2012, Spout LLC <<http://www.spout.org/>>

## Getting the Source
The latest and greatest source can be found here on [GitHub][Source].  
Download the latest builds from our [build server][Builds]. [![Build Status](http://build.spout.org/job/SpoutcraftLauncher/badge/icon)][Builds]

## Compiling the Source
Spoutcraft Launcher uses Maven to handle its dependencies.

* Install [Maven 2 or 3](http://maven.apache.org/download.html)
* Checkout this repo and run: `mvn clean package`
* To compile an `exe` on a non-Windows platform, add: `-P package-win` to the previous goals.

## Contributing to the Project
Track and submit issues and bugs on our [issue tracker][Issues].  
[Share the love!][Donate] Donations help make development possible!

## Code and Pull Request Formatting
* Generally follow the Oracle coding standards.
* No spaces, only tabs for indentation.
* No trailing whitespaces on new lines.
* 200 column limit for readability.
* Pull requests must compile, work, and be formatted properly.
* Sign-off on ALL your commits - this indicates you agree to the terms of our license.
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Number of commits in a pull request should be kept to *one commit* and all additional commits must be *squashed*.
* You may have more than one commit in a pull request if the commits are separate changes, otherwise squash them.
* For clarification, see the full pull request guidelines [here](http://spout.in/prguide).

**Please follow the above conventions if you want your pull request(s) accepted.**

[Homepage]: http://www.spout.org
[Forums]: http://forums.spout.org
[License]: http://spout.in/licensev1
[Source]: https://github.com/SpoutDev/SpoutcraftLauncher
[Issues]: https://spoutdev.atlassian.net/browse/SPOUTCRAFT
[Twitter]: http://spout.in/twitter
[Facebook]: http://spout.in/facebook
[Donate]: http://spout.in/donate
