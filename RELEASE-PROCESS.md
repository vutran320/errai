Errai Project Release Process
=============================

This document describes the steps that should be followed during every release of
the Errai project. When making a release, please be careful to follow these
steps in order. If something doesn't make sense, if you perform steps not listed
here, or if after the release, it was discovered that something was forgotten,
please update these notes to reflect the correct procedure.

The hope is that most or all of these steps will be automated. But a necessary
first step is to discover everything that needs to be done in order to make a
release successful.

A note on Errai Theory
----------------------

When the Maven Release Plugin goes awry, it's a tedious process to back out its
automated git tags and commits. We prefer to follow the steps manually, committing
after each step as we go.

Release Steps
-------------

1. Run the test suite. Ensure all tests pass.

        % mvn -Pintegration-test test
   
1. Update reference guide with latest content in confluence
   (Note that this automatically downloads the latest docs and adds `language` attributes to all the `programlisting` elements.
   You need to have a file `jboss-credentials.properties` in the `reference` directory with your JBoss Community username and
   password under the keys `jboss.username` and `jboss.password` respectively.)
      ```
         % cd reference
         % mvn package   # this needs a profile in ~/.m2/settings.xml that references the JBoss public maven repo
         % git add src
      ```
  * Edit the version numbers in Book_Info.xml to reflect release version
  * Don't upload to JBoss FTP server! The release upload script will do this later.

1. Ask Maven to update the version number in all the pom.xml files:
   
        % cd $errai_root_dir
        % ./updateVersions.sh a.b.c.Final x.y.z.Final
   
   Afterward, verify that all subprojects reference the new parent pom's version:
   
        % find . -name pom.xml | xargs grep x.y.z | grep SNAP
       
   (if any are out of sync with the parent version, Maven will not have updated them)

1. Build and package the release. These are the bits that will be uploaded to nexus.
   Expect this to take about 4 minutes, depending on network speed.

        % mvn clean deploy -Dgwt.compiler.skip=true -DaltDeploymentRepository=jboss-snapshots-repository::default::https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/

1. Create the a-la-carte binary Errai distribution and docs

        % mvn install -Pdistro -Dmaven.test.skip=true -Dgwt.compiler.skip=true
        
1. Deploy the Errai Cordova project template

        % sudo ./errai-cordova-maven-plugin/src/main/bash/cordova-bundle a.b.c.Final

1. Upload the docs and the distro zipfile

        % cd dist
        % scripts/upload_binaries.sh ${version}

1. Tag and push the release to github

        % git commit a -m "Updated to new version x.y.z"
        % git tag x.y.z.Final
    
  reset all versions to x.y.z+1-SNAPSHOT and commit
  
        % git push origin /branch/
        % git push origin --tags
        % git push upstream /branch/
        % git push upstream --tags

1. Browse to nexus (https://repository.jboss.org/nexus/index.html)
  * Find the corresponding staging repository (Sort by repository name)
  * Select it and click Close
  * Select it again and click Release
  * Browse to https://repository.jboss.org/nexus/content/groups/public/org/jboss/errai/ and verify that 
     the artifacts are present

1. Update the version number of errai in the getting started demo's pom.xml:
  https://github.com/errai/summit-demo-2013/blob/master/pom.xml

1. Update http://www.jboss.org/errai/Documentation to provide the download links for
   the generated/released docs and distribution.

1. Update the announcement on the welcome page (https://www.jboss.org/author/)

1. Publish a release blog entry at http://errai-blog.blogspot.com/

1. Tweet about the release!

=== You're done! Congrats! You deserve beer! ===
