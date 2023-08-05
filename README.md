# Catawiki Android Assignment - Pokemon App

Pokemon App with paginated list of pokemon species and details screen with description 
and next evolution chain pokemon

## Architecture pattern used

CLEAN Arch with MVVM for the presentation layer
Viewmodel depends on the domain layer which exposes UseCases for getting business data
Viewmodel only handles UI data and doesnt do any business logic but rather delegates it to the usecase
Usecases get their raw data from repositories which are implemented in the data layer 
and applies business logic on them to make it fit for consumption by the presentation layer

# Domain Layer
A simple layer which exposes contracts for Repositories and  data model classes which the data 
layer must adhere. It also houses any business logic which must be applied to raw data given by
repositories. Also houses usecases which are the communication channel between ui and biz. logic

# Presentation Layer(Compose + MVVM + RXJava)

1. For the UI Layer : Compose + MVVM with RXJava for State management.
UI listens to RX state holders which in turn produce correct states for any user interaction. 
2. Viewmodel exposes RXjava subjects as Observables to UI
3. Compose UI subscribes to these observables via `subscribeAsState()` function 
   which is part of compose-rx inter-op library
4. ViewModel takes data from usecase and converts it to relevant UI States and pushes them into 
   state observables

# Data Layer (RXJava + Retrofit + Room + Moshi)
1. REST Api calls are done to the Poke API GQL server 
2. GQL calls are simple POST calls but are much more lighter because we can control the exact data 
   needed by our application. This eliminates a lot of unnecessary mapping, saves network bandwidth
   and takes less local storage because we dont have to save large DTOs into Db
2. Room is used for local storage.
3. Whenever Usecase requests data, repository checks if local storage can serve the request. 
   if there is no data, or db throws an error, repo calls the rest api.
4. Moshi is used as the converter for json parsing becuase it doesnt rely on reflection parsing but
   rather generates its own type safe non-reflection json adapters in build time, much like dagger. 
   it also provides compile time safety incase we want to convert/read some json manually and supports
   custom converters and converter factories



## Test Coverage
Currently the project has 87% test coverage with some packages having 100% coverage.
Packages include only the logic part viz data, domain and viewmodels.
Test cases for the DB are also included

## Libraries Used
Compose Libraries for UI
Standard Android KTX Libraries
Hilt for DI
Hilt Navigation Compose injecting VMs in Compose Nav Graph Support 
Palette for getting the background color based on dominant color of image
Retrofit, OkHttp for Network calls
Moshi for Json parsing
Room for local db
JUnit and Mockito for Unit tests
MockWebServer for mocking api requests
