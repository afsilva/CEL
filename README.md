CEL - Cloud Engine Light
===
my personal playground for android programming

It is my attempt to provide useful information about Red Hat's Openshift with this app. Yet this  project is not associated with Red Hat or
Openshift at all.

master branch (current version, v1.5.3)
- changed all external links to use ACTION_VIEW instead of Webview class.

(v1.5.2)
- fix preferences wording
- try to maintain smaller cache for webview

(v1.5.1)
- if Openshift status recovers, it will cancel the notification.

(v1.5)
- w00t! I now have preferences working on the app. You can now turn auto check on and off, and set the frequency of checks against the Openshift status server.

(v1.4.3)
- When I merged v1.4.2, I deleted the changes from v1.4.1 by accident. This version fixes it all of it.

(v1.4.2)
- updated the notification class, so it doesn't use deprecated stuff
- clicking on a notification will now take you to the built-in CEL browser, instead of opening up the stock device browser.

(v1.4.1)
- make sure notification only comes up if there is an open issue on Openshift. No notification for network connectivity on the device.

(v1.4)
- app now supports alarm manager and checks against Openshift status API once an hour. It only alerts if there is an open issue.
