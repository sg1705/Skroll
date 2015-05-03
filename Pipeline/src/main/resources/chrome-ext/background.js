// React when a browser action's icon is clicked.
chrome.browserAction.onClicked.addListener(function(tab) {
   chrome.tabs.getSelected(null,function(tab) {
        chrome.tabs.create( { url: "http://localhost:8080/Pipeline/start.html#/open?q=" +tab.url } );
    });
});