# Walkthrough - Added Auth Interceptor to Vision API

I have successfully integrated the authentication interceptor (login interceptor) into all camera search (Vision API) calls. This ensures that the `Authorization: Bearer <token>` header is included in these requests, matching the behavior of other API calls in the app.

## Changes

### UI Components

I modified the following fragments to use the Hilt-injected `ApiServices` instead of the standalone `VisionRetrofit`:

- [SearchProductListFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/store/SearchProductListFragment.kt)
- [ProductSearchFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/store/ProductSearchFragment.kt)
- [HomeFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/fragment/HomeFragment.kt)
- [AffiliateAllProductFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/affiliateUser/home/product/AffiliateAllProductFragment.kt)
- [AffiliateSearchProductListFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/affiliateUser/home/product/AffiliateSearchProductListFragment.kt)
- [SearchAffiliateProductFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/affiliateUser/home/product/SearchAffiliateProductFragment.kt)

### Implementation Details

1.  **Dependency Injection**: Added `@Inject lateinit var apiServices: ApiServices` to each fragment.
2.  **API Call Update**: Replaced `VisionRetrofit.api.detectImage(...)` with `apiServices.detectImage(...)`.
3.  **Cleanup**: Removed unused `VisionRetrofit` imports from the above fragments and [LiveEventFragment.kt](file:///C:/Users/tausi/Android%20Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/storyHighlights/LiveEventFragment.kt).

## Verification Results

### Automated Tests
- Executed `gradlew :app:assembleDebug` - **Build successful**.

### Manual Verification
- (User) You can now verify that the Vision API requests include the `Authorization` header by checking the network logs (OkHttp logging interceptor will now show these calls).
