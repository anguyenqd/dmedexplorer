# Comic Explorer
Comic Explorer is an application that displays comics from Marvel in a list and provides a detailed screen showing a full description of a comic. It also supports deeplink (dmed://marvel?query={comicId}) to navigate to the comic detail screen.

# Demo
https://user-images.githubusercontent.com/3113106/176392366-a3c72c3d-64f8-49b6-bdba-a9f299863030.mp4

## Architecture
The application has two Activities:
- `ViewComicListActivity`: Display a Comic list and allow users to click on a comic to navigate to the `ViewSingleComicActivity`.
- `ViewSingleComicActivity`: Display a comic's detail (title, original image, and description). Users can reach this activity from `ViewComicListActivity` or by a deeplink.

Clean architecture is applied, which splits the project into four modules:
- app: Contains necessary Android SDK set up at application level and dependency injection (Hilt-Dagger) module declaration.
- domain: Contains the abstract interfaces for the repository, data models, and State Machines.
- view: Contains the UI-related classes and app logic
- data: Contains the repository implementation to store and retrieve data from Marvel API

Repository Pattern:
The data module implements the pattern from the `Repository` interface, which controls two data sources to store (`LocalDataSource`) and retrieve data (`NetworkDataSource`) from Marvel API. 
In general, data is stored by `LocalDataSource` will be retrieved by use-cases under the support of `Flow`. By using `Flow`, any update in the local database can be updated automatically to the consumer. `NetworkDataSource` calls Marvel APIs for comics data and `Repository` update them into the local database.

## Deeplink
Users can be directly navigated to a comic detail's view by using this URL `dmed://marvel?query={comicId}`
Test: `adb shell am start -W -a android.intent.action.VIEW -d "dmed://marvel?query=96381" com.annguyen.dmedandroiassignment`

## Testing
- Unit tests: `ViewSingleComicViewModelTest` in `view` module fake the repository implementation to test the `ViewSingleComicViewModel`. The `requestComicDataThenRefreshWithSameComicId` tests the process that the view (`ViewSingleComicActivity`) feeds a comic id into the ViewModel to get back the detailed data. The process also includes refreshing the comic's detail data from API. In technical, the view collects data from the flow that hooks into a row in the local database, in the case when the data is not available, and there is no data is returned into the collector. When the network data is returned, the repository add/update into the local database then it will be pushed up by `Flow` to the collector. 

- UI Test: `ViewSingleComicScreenInstrumentedTest` in `app` module fake the network data source implementation to return fake data. With this setup, the UI test is robust without any flaky and the need to maintain live data in a test API. 

## State Machine
This application uses state machine library from Tinder (https://github.com/Tinder/StateMachine) to manage the states of UI and data coordination for every screen. Every state represents a single UI state of a screen.
- `ViewComicListStateMachine`: It is the state machine that manage the states of UI and data coordination in `ViewComicListActivity`. 
![viewComicListStateMachine](https://user-images.githubusercontent.com/3113106/176402593-09914003-d6eb-4bc1-965d-caf8a8623eee.jpeg)
- `ViewSingleComicStateMachine`:  It is the state machine that manage the states of UI and data coordination in `ViewSingleComicActivity`.
![ViewSingleComicStateMachine](https://user-images.githubusercontent.com/3113106/176403260-6166fbb8-4393-4a8b-bc80-25814553c521.jpeg)

## External libraries are being used:
- Picasso: for loading and caching images.
- Room: for local database.
- Coroutines: for asynchronous tasks. 
- flow: for the observable pattern. 
- Tinder's state machine: For state machine. 
- Hilt: For dependency injection. 
- Pagination: for infinite scroll support.
- Shimmer: for loading animation
- Mockito: for mocking in testing.

## Generating hash
Hash is generated with this pattern: md5 digest of timestamp + private key + public key. 

As the warning on Marvel API page, the private key should not be exposed, which should not be included in the source code. 

The timestamp is supposed to be unique for every request to the API but that conflicts with the requirement for the private key. If we generate a new timestamp for every request, the app needs to know about the private key to re-generate the hash. 

For this project, there is no secured external API to securely provide the private key, so I keep it simple by generating the hash beforehand and hard-coding it in the source code. 
