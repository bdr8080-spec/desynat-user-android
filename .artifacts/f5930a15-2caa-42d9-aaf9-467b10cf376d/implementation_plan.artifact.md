# Fix Data Retention Glitch in StoreDetailFragment

The user is experiencing a glitch where clicking on a store sometimes shows data from the previously visited store. This happens because the `HomeViewModel` is shared via `activityViewModels()`, and its `LiveData` retains stale data which is immediately delivered to new `StoreDetailFragment` instances upon subscription.

## Proposed Changes

### [HomeViewModel](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/data/view_model/HomeViewModel.kt)

#### [MODIFY] [HomeViewModel.kt](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/data/view_model/HomeViewModel.kt)
- In `getSingleStoreInfo()`, clear the existing `storeDetailLiveData` value by setting it to `null` before starting the new API call.
- In `getSellerStory()`, clear `getSellerStoryLiveData`.
- In `getSellerReel()`, clear `getSellerReelLiveData`.
- In `getSellerHighlights()`, clear `getSellerHighlightsLiveData`.

This ensures that when a new `StoreDetailFragment` starts observing these `LiveData`, it won't receive stale data from a previous session.

## Verification Plan

### Manual Verification
- Deploy the app.
- Navigate to `StoreFragment`.
- Click on Store A, then go back.
- Click on Store B.
- Verify that Store B's details load directly (or show a loader) without briefly showing Store A's data.
- Verify that Reels and Highlights also update correctly and don't show old content.
