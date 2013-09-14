import android.app.Service;
import android.app.NofificationManager;
import android.app.Binder;

public class DownloadService extends Service {
    private NotificationManager notMgr;

    public class LocalBinder extends Binder {
        LocalService getServices() {
            return LocalService.this;
        }
    }
}
