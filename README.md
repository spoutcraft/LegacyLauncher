Technic Launcher
===================

## What is The Technic Launcher?
The Technic Launcher is a Minecraft launcher designed to install and manage modpacks from the [Technic Platform][Homepage].
Features automatic updating, custom platform pack installation, and easy simple UI design.
The launcher in its current form is maintained by [CanVox](https://github.com/CannibalVox)

[![Technic][Logo]][Homepage]  
[Homepage] | [Forums] | [Twitter] | [Facebook] | [Steam]

## The License
The Technic Launcher is licensed under the [GNU General Public License Version 3][License]. Please see the `LICENSE.txt` file for details.

Copyright (c) 2013 Syndicate, LLC <<http://www.technicpack.net/>>

## Getting the Source
The latest and greatest source can be found here on [GitHub][Source].  
Download the latest builds from our [build server][Builds]. [![Build Status](http://build.technicpack.net/job/TechnicLauncher/badge/icon)](http://build.technicpack.net/job/TechnicLauncher/)

## Compiling the Source
Technic Launcher uses Maven to handle its dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Checkout this repo and run: `mvn clean package`
* To compile an `exe` on a non-Windows platform, add: `-P package-win` to the previous goals.

## Contributing to the Project
Track and submit issues and bugs on our [GitHub issues page][Issues].  

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
* For clarification, follow the Spout pull request guidelines [here](http://spout.in/prguide).

**Please follow the above conventions if you want your pull request(s) accepted.**

[Logo]: http://i.imgur.com/PCI0pIo.png
[Homepage]: http://www.technicpack.net
[Forums]: http://forums.technicpack.net
[License]: http://www.gnu.org/licenses/gpl-3.0.txt
[Source]: https://github.com/TechnicPack/TechnicLauncher
[Builds]: http://build.technicpack.net/job/TechnicLauncher/
[Issues]: https://github.com/TechnicPack/TechnicLauncher/issues
[Twitter]: https://twitter.com/TechnicPack
[Facebook]: https://www.facebook.com/TechnicPack
[Steam]: http://steamcommunity.com/groups/technic-pack
