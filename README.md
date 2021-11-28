# Tic Tac Toe Kotlin MPP
This repository was created for my JetBrains Internship application.

This is a MVP implementation. I sadly did not have much time to invest into this but I'm pretty sure the "AI" is almost unbeatable. But it works. That's the most important part.

It supports multiple game instances. So technically this server could be spun up and 
multiple people could play tic tac toe against my "AI". 

Before creating this I've never used Kotlin before. Same holds for the entire framework (Kotr, mpp etc..)
So I had to teach myself the entire stack and code it all on the same day.

I only had 1 day to work on this and the documentation is somewhat lacking. But I think
it turned out fine considering all these constraints. Let me know if you're able to beat the AI :)

### Disclaimer
The webpage is not responsive. This is not very complicated, but very time consuming. 
So I've created it with 1080p in mind and everything else is basically at your own risk. 

Also the frontend does not do any error handling. This again is not hard to add, but would just
pollute the demo code and take a while. Thus, I've decided to not add it. 

### Things that could be added
- Proper multiplayer. Technically this allows for multiple games to be run. So concurrent multiplayer shouldnt be hard to add
- Websocket implementation. This also means we can send incremental changes instead of the entire board
- Actual responsive design