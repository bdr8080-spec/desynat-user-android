# Implementation Plan - Sync Reel View Designs and Add Pause Functionality

The goal is to synchronize the design of `fragment_view_reel.xml` with `fragment1_view_reel.xml` and implement a "click anywhere to pause/play" feature for reels.

## User Review Required

> [!IMPORTANT]
> Both `ViewReelFragment` and `ViewReelFragment1` will now use a `RecyclerView` with `ViewReelAdapter`. This ensures a consistent look and feel (paginated vertical scrolling).
> Clicking anywhere on the reel will now toggle play/pause, showing a play icon in the center when paused.

## Proposed Changes

### [Layouts]

#### [MODIFY] [fragment_view_reel.xml](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/res/layout/fragment_view_reel.xml)
- Change the layout to match `fragment1_view_reel.xml`, containing a `tvToolbar` and a `rvReel` (RecyclerView).

#### [MODIFY] [item_view_reel.xml](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/res/layout/item_view_reel.xml)
- Ensure the layout is optimized for full-screen video and that the center `ivPlay` icon is ready for visibility toggling.

### [Adapter]

#### [MODIFY] [ViewReelAdapter.kt](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/adapter/ViewReelAdapter.kt)
- Add a click listener to the root layout of each item.
- Implement play/pause toggle logic on root click.
- Update `ivPlay` visibility: show it when the video is paused, hide it when playing.

### [Fragments]

#### [MODIFY] [ViewReelFragment.kt](file:///C:/Users/tausi/Android Projects/Desynate/desynat-user-android/app/src/main/java/com/desynatuser/ui/home/storyHighlights/ViewReelFragment.kt)
- Update to use `ViewReelAdapter` and `RecyclerView` instead of direct `VideoView` management.
- Wrap the single `reelData` into a list to pass to the adapter.

## Verification Plan

### Automated Tests
- N/A (UI specific changes)

### Manual Verification
1. Navigate to Reels from `StoreDetailFragment` (both `viewReel` and `viewReel1` paths).
2. Verify that `ViewReelFragment` now looks identical to `ViewReelFragment1`.
3. Click anywhere on the video in both screens.
4. Verify the video pauses and a play icon appears in the center.
5. Click again to resume and verify the icon disappears.
