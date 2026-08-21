# Add Auth Interceptor to Vision API Calls

The user wants to add the "login interceptor" (the authentication interceptor that adds the Bearer token) to the `callVisionApi` function. Currently, this function uses `VisionRetrofit.api`, which uses a standalone `OkHttpClient` without any interceptors.

The most idiomatic way to achieve this in this Hilt-enabled project is to inject the `ApiServices` instance provided by `AppModule`, as it already contains the necessary interceptor logic for authorization and logging.

## User Review Required

> [!IMPORTANT]
> The "login interceptor" adds an `Authorization: Bearer <token>` header to the request. Since the Vision API is a Google service (`vision.googleapis.com`), it typically relies on the `key` query parameter (which is already provided). Adding an extra Bearer token might be ignored by Google, but if your backend or a proxy requires it for tracking/security, this change will fulfill that requirement.

## Proposed Changes

### [Component] UI Fragments

I will update the fragments to inject `ApiServices` and use it for the Vision API call.

#### [MODIFY] [SearchProductListFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/store/SearchProductListFragment.kt)
- Add `@Inject lateinit var apiServices: ApiServices`.
- Replace `VisionRetrofit.api.detectImage` with `apiServices.detectImage` in `callVisionApi`.
- Remove `VisionRetrofit` import.

#### [MODIFY] [ProductSearchFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/store/ProductSearchFragment.kt)
- Add `@Inject lateinit var apiServices: ApiServices`.
- Replace `VisionRetrofit.api.detectImage` with `apiServices.detectImage` in `callVisionApi`.
- Remove `VisionRetrofit` import.

*Note: I will also apply this to other fragments that implement `callVisionApi` using `VisionRetrofit` to ensure consistency.*

## Verification Plan

### Automated Tests
- I will check for successful compilation by running a Gradle build for the `:app` module.
- `gradlew :app:assembleDebug`

### Manual Verification
- Verify that the camera search still works as expected.
- (User) Check network logs to see if the `Authorization` header is now present in the Vision API request.
