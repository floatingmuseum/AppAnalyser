// IPackageStatsObserver.aidl
package android.content.pm;

// Declare any non-default types here with import statements

interface IPackageStatsObserver {
        oneway void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
}
