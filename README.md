# Comic Explorer
This is an application that display comics from Marvel in a list and also provide a detail screen that show the full description of a comic. It also supports deeplink to navigate to the comic detail screen.

# Demo
![](docs/demo.mp4)

## Architecture
The application has two Activities:
- `ViewComicListActivity`: Display a list of Comic and allow user to click on a comic to navigate to the `ViewSingleComicActivity`.
- `ViewSingleComicActivity`: Display detail (title, original image and description) of a comic. User can reach this activity from `ViewComicListActivity` or by a deeplink.

Clean architecture is applied which split the project into 4 modules:
- app: Contain necessary Android SDK set up in application level and dependency injection (Hilt-Dagger) module declaration.
- domain: Contain the abstract interfaces for repository, 
- view
- data


