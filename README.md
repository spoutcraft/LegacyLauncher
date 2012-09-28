Spoutcraft Launcher
===================
Spoutcraft Launcher is the custom launcher for installing and updating Spoutcraft with built-in byte patching for easy upgrading/downgrading of Minecraft versions.

Visit our [website][Website] or get support on our [forums][Forums].  
Track and submit issues and bugs on our [issue tracker][Issues].

[![Follow us on Twitter][Twitter Logo]][Twitter][![Like us on Facebook][Facebook Logo]][Facebook][![Donate to the Spout project][Donate Logo]][Donate]

## The License
Spoutcraft Launcher is licensed under the [GNU Lesser General Public License Version 3][License], but with a provision that files are released under the MIT license 180 days after they are published. Please see the `LICENSE.txt` file for details.

Copyright (c) 2011-2012, SpoutDev <<http://www.spout.org/>>  
[![Spout][Author Logo]][Website]

## Getting the Source
The latest and greatest source can be found on [GitHub].  
Download the latest builds from [Jenkins]. [![Build Status](http://build.spout.org/job/SpoutcraftLauncher/badge/icon)][Jenkins]

## Compiling the Source
Spoutcraft Launcher uses Maven to handle its dependencies.

* Install [Maven 2 or 3](http://maven.apache.org/download.html)
* Checkout this repo and run: `mvn clean package`

## Code and Pull Request Formatting
* Generally follow the Oracle coding standards.
* Use tabs for indentation, not spaces.
* No trailing whitespaces.
* 200 column limit for readability.
* Pull requests must compile, work, and be formatted properly.
* Sign-off on ALL your commits - this indicates you agree to the terms of our license.
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Number of commits in a pull request should be kept to *one commit* and all additional commits must be *squashed*.
* You may have more than one commit in a pull request if the commits are separate changes, otherwise squash them.
* When modifying Notch code (Minecraft code), include //Spout start and //Spout end
* For clarification, see the full pull request guidelines [here](http://spout.in/prguide).

**Please follow the above conventions if you want your pull request(s) accepted.**

[Author Logo]: http://cdn.spout.org/img/logo/spout_305x135.png
[License]: http://www.spout.org/SpoutDevLicenseV1.txt
[Website]: http://www.spout.org
[Forums]: http://forums.spout.org
[GitHub]: https://github.com/SpoutDev/SpoutcraftLauncher
[Jenkins]: http://build.spout.org/job/SpoutcraftLauncher
[Issues]: http://issues.spout.org/browse/spoutcraft
[Twitter]: http://spout.in/twitter
[Twitter Logo]: http://cdn.spout.org/img/button/twitter_follow_us.png
[Facebook]: http://spout.in/facebook
[Facebook Logo]: http://cdn.spout.org/img/button/facebook_like_us.png
[Donate]: http://spout.in/donate
[Donate Logo]: http://cdn.spout.org/img/button/donate_paypal_96x96.png
