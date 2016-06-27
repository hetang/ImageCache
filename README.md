# Image Cache
Implementing Image Cache with LRU algo. Trying to update and retrival of cache object in O(1) time. I am using Map collection object with Doubly linked list of nodes.

##Steps to import & Run project
1. Download Intellij IDE to use this project
2. Click import project option and select source code folder
3. This will create ImageCache Java project in Intellij
4. Now you can run all unittest and explore more about implementation.

##Cutomization
I am using factory and singleton pattern to link between ImageCache and LRUCache classes. This helps to make customization simple. If you wish to use different caching stretegy, Please add implementation in project and link with ImageCacheFactory class.
