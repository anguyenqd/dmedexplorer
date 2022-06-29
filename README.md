# Comic Explorer
This is an application that display comics from Marvel in a list and also provide a detail screen that show the full description of a comic. It also supports deeplink to navigate to the comic detail screen.

# Demo
https://user-images.githubusercontent.com/3113106/176392366-a3c72c3d-64f8-49b6-bdba-a9f299863030.mp4

## Architecture
The application has two Activities:
- `ViewComicListActivity`: Display a list of Comic and allow user to click on a comic to navigate to the `ViewSingleComicActivity`.
- `ViewSingleComicActivity`: Display detail (title, original image and description) of a comic. User can reach this activity from `ViewComicListActivity` or by a deeplink.

Clean architecture is applied which split the project into 4 modules:
- app: Contains necessary Android SDK set up in application level and dependency injection (Hilt-Dagger) module declaration.
- domain: Contains the abstract interfaces for repository, data models and State Machines.
- view: Contains the UI related classes and app logic
- data: Contains the respository implementation to store and retrieve data from Marvel API

Repository Pattern:
The data module implements the pattern from the `Repository` interface which controls two data sources to store (`LocalDataSource`) and retrieve data (`NetworkDataSource`) from Marvel API. 
In general, data is store by `LocalDataSource` will be retrieved by usecases under the support of `Flow`. By using `Flow`, any update in the local database can be updated automatically to the consumer. `NetworkDataSource` calls Marvel APIs for comics data and `Repository` update them into the local database.

## Testing
- Unit tests: `ViewSingleComicViewModelTest` in `view` module fake the repository implementation to test the `ViewSingleComicViewModel`. The `requestComicDataThenRefreshWithSameComicId` tests the process that the view (`ViewSingleComicActivity`) feed a comic id into the viewModel to get back the detail data. The process also includes refreshing the detail data from API. In technical, the view collects data from the flow that hook into a row in the local database, in the case when the data is not available, there is no data is returned into the collector. When the network data is returned, the repository add/update into the local database then it will be stream up by `Flow` to the collector. 

- UI Test: `ViewSingleComicScreenInstrumentedTest` in `app` module fake the network data source implemenetation to return fake data. With this setup, the UI test is roburst without any flaky and necessity to maintain live data in a test API. 

## State Machine
This application use state machine library from Tinder (https://github.com/Tinder/StateMachine) to manage the states of UI and data cooridination for every screen. Every state represent a single UI state of a screen.
- `ViewComicListStateMachine`: It is the state machine which manage the states of UI and data coordination in `ViewComicListActivity`. 
![viewComicListStateMachine](https://user-images.githubusercontent.com/3113106/176402593-09914003-d6eb-4bc1-965d-caf8a8623eee.jpeg)
- `ViewSingleComicStateMachine`:  It is the state machine which manage the states of UI and data coordination in `ViewSingleComicActivity`.
![ViewSingleComicStateMachine](https://user-images.githubusercontent.com/3113106/176403260-6166fbb8-4393-4a8b-bc80-25814553c521.jpeg)

## External libraries are being used:
- Picasso: for loading and cache images.
- Room: for local database.
- Coroutines: for asynchronous tasks. 
- flow: for observable pattern. 
- Tinder's state machine: For state machine. 
- Hilt: For dependency injection. 
- Pagination: for infinite scroll support.
- Shimmer: for loading animation
- Mockito: for mocking in testing.

## Generating hash
Hash is the generated with this pattern: md5 digest of timestamp + private key + public key. 

As the warning in marval api page, private key should not to be exposed which should not be included in the source code. 

Timestamp supposed to be unque for every request to the API but that conflict with the requirement for private key. If we generate new timestamp for every request, the app need to know about the private key to re-generate the hash. 

For this project, there is no secured external API to provide the private key securely so I go with keeping it simple by generate the hash beforehand and hard code it in the source code. 
