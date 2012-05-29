[![Spout][Project Logo]][Website]

What is Spoutcraft Launcher?
----------------------------
Spoutcraft Launcher is the custom launcher for installing and updating Spoutcraft with built-in byte patching for easy upgrading/downgrading of Minecraft versions. It also features an API for customization and skins.

Copyright (c) 2011-2012, SpoutDev <<http://www.spout.org/>>

Who is SpoutDev?
----------------
SpoutDev is the team behind the Spout and Spoutcraft projects, I See You, and Pipe.    
[![Afforess](https://secure.gravatar.com/avatar/ea0be49e1e4deac42ed9204ffd95b56c?d=mm&r=pg&s=48)](http://forums.spout.org/members/afforess.2/) [![alta189](https://secure.gravatar.com/avatar/7a087430b2bf9456b8879c5469aadb95?d=mm&r=pg&s=48)](http://forums.spout.org/members/alta189.3/) [![Wulfspider](https://secure.gravatar.com/avatar/6f2a0dcb60cd1ebee57875f9326bc98c?d=mm&r=pg&s=48)](http://forums.spout.org/members/wulfspider.1/) [![raphfrk](https://secure.gravatar.com/avatar/68186a30d5a714f6012a9c48d2b10630?d=mm&r=pg&s=48)](http://forums.spout.org/members/raphfrk.601/) [![narrowtux](https://secure.gravatar.com/avatar/f110a5b8feacea25275521f4efd0d7f2?d=mm&r=pg&s=48)](http://forums.spout.org/members/narrowtux.5/) [![Top_Cat](https://secure.gravatar.com/avatar/defeffc70d775f6df95b68f0ece46c9e?d=mm&r=pg&s=48)](http://forums.spout.org/members/top_cat.4/) [![Olloth](https://secure.gravatar.com/avatar/fa8429add105b86cf3b61dbe15638812?d=mm&r=pg&s=48)](http://forums.spout.org/members/olloth.6/) [![Rycochet](https://secure.gravatar.com/avatar/b06c12e72953e0edd3054a8645d76791?d=mm&r=pg&s=48)](http://forums.spout.org/members/rycochet.10/) [![RoyAwesome](https://secure.gravatar.com/avatar/6d258213c33a16465021daa8df299a0d?d=mm&r=pg&s=48)](http://forums.spout.org/members/royawesome.8/) [![zml2008](https://secure.gravatar.com/avatar/2320ab48d0715a4e9c73b7ec13fd6f3a?d=mm&r=pg&s=48)](http://forums.spout.org/members/zml2008.14/) [![Zidane](https://secure.gravatar.com/avatar/99532c7f117c8dac751422376116fb38?d=mm&r=pg&s=48)](http://forums.spout.org/members/zidane.7/) 

Visit our [website][Website] or get support on our [forums][Forums].  
Track and submit issues and bugs on our [issue tracker][Issues].

[![Follow us on Twitter][Twitter Logo]][Twitter][![Like us on Facebook][Facebook Logo]][Facebook][![Donate to the Spout project][Donate Logo]][Donate]

Source
------
The latest and greatest source can be found on [GitHub].  
Download the latest builds from [Jenkins].  
View the latest [Javadoc].

License
-------
Spoutcraft Launcher is licensed under [GNU Lesser General Public License Version 3][License], but with a provision that files are released under the MIT license 180 days after they are published. Please see the `LICENSE.txt` file for details.

Compiling
---------
Spoutcraft Launcher uses Maven to handle its dependencies.

* Install [Maven 2 or 3](http://maven.apache.org/download.html)
* Checkout this repo and run: `mvn clean package install`

For those using [Maven](http://maven.apache.org/download.html) to manage their project dependencies, simply include the following in your pom.xml:

    <dependency>
        <groupId>org.spoutcraft</groupId>
        <artifactId>launcher</artifactId>
        <version>2.0.0-SNAPSHOT</version>
    </dependency>

If you do not already have repo.spout.org in your repository list, you will need to add this also:

    <repository>
        <id>spout-repo</id>
        <name>Spout project maven repo</name>
        <url>https://repo.spout.org</url>
    </repository>

Code and Pull Request Formatting
----------------------------------
* Generally follow the Oracle coding standards.
* Use tabs for indentation, not spaces.
* No trailing whitespaces.
* 200 column limit for readability.
* Pull requests must compile, work, and be formatted properly.
* Sign-off on ALL your commits - this indicates you agree to the terms of our license.
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Number of commits in a pull request should be kept to *one commit* and all additional commits must be *squashed*.
* You may have more than one commit in a pull request if the commits are separate changes, otherwise squash them.
* For clarification, see the full pull request guidelines [here](http://spout.in/prguide).

**Please follow the above conventions if you want your pull request(s) accepted.**

[Project Logo]: http://cdn.spout.org/img/logo/spout_327x150.png
[License]: http://www.spout.org/SpoutDevLicenseV1.txt
[Website]: http://www.spout.org
[Forums]: http://forums.spout.org
[GitHub]: https://github.com/SpoutDev/SpoutcraftLauncher
[Javadoc]: http://jd.spout.org/legacy/launcher/
[Jenkins]: http://build.spout.org/job/SpoutcraftLauncher
[Issues]: http://issues.spout.org
[Twitter]: http://spout.in/twitter
[Twitter Logo]: http://cdn.spout.org/img/button/twitter_follow_us.png
[Facebook]: http://spout.in/facebook
[Facebook Logo]: http://cdn.spout.org/img/button/facebook_like_us.png
[Donate]: https://www.paypal.com/cgi-bin/webscr?hosted_button_id=QNJH72R72TZ64&item_name=Spoutcraft+Launcher+donation+%28from+github.com%29&cmd=_s-xclick
[Donate Logo]: http://cdn.spout.org/img/button/donate_paypal_96x96.png
