package katas.ex16

// TODO: Hilt modules and the @IoDispatcher qualifier go here.
//
// The tests expect:
// - a module `ApiModule` (object) that provides PrescriptionApi (the test replaces it),
// - a module `DataModule` (abstract class) that binds PrescriptionsRepository to DefaultPrescriptionsRepository as a singleton,
// - a module `DispatchersModule` (object) with `@IoDispatcher CoroutineDispatcher` = Dispatchers.IO,
// - a qualifier annotation `@IoDispatcher`.
