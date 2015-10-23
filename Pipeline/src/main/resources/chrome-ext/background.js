// React when a browser action's icon is clicked.
chrome.browserAction.onClicked.addListener(function(tab) {
   chrome.tabs.getSelected(null,function(tab) {
        chrome.tabs.create( { url: "http://skroll.io/open?q=" +tab.url } );
    });
});
