# CrossGit
A cross plaform JavaFX GUI client for git (still at proof-of-concept, pre-alpha stage). Uses [JGit](https://github.com/eclipse/jgit), inspired by [Git Extensions](https://github.com/gitextensions/gitextensions).

## Netbeans config
1. clone ``https://github.com/lodici/CrossGit.git``.
2. extract ``netbeans-config.zip`` to root folder to create ``nbproject`` sub-folder.
3. start Netbeans then File -> Open Project to load CrossGit source.
4. there will be lots of errors and a dialog will probably be shown indicating there are problems. Ignore and close the dialog.
5. right click on the CrossGit node in the Projects panel and select Build from the menu.
6. Netbeans should download the missing dependencies (the cause of the errors) and build successfully.
