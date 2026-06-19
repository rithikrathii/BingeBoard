AI Reflection — Android Frontend
**by: Rithik Kumar - 31522**

---

## 1. Tools Used

Claude (Anthropic, Claude Sonnet 4.5)

---

## 2. Usage Protocol

I want to be transparent — I used Claude a lot throughout this project. The base architecture (MVVM structure, Hilt setup, navigation, all screens) was built by following YouTube tutorials including Code with Harry and various Android development videos, and through trial and error myself. But once the backend integration started, I used Claude heavily for debugging, understanding concepts, and generating code.

The examples below are only a small selection of the conversations I had with Claude. In reality there were hundreds of back and forth messages — pasting Logcat errors, trying fixes, breaking something else, coming back with a new error, and going again. Every fix Claude suggested I tested on the emulator myself and modified when it did not work for my exact setup. The examples I have documented here are the ones that had the biggest impact on the final working state of the app.

Another area where Claude was heavily used was implementing new functions. When I needed something I had not done before — like the lazy scroll refactor, the custom Gson deserializer, or the review delete logic — I would describe what I needed and Claude would write the function with an explanation of why each part worked that way. Sometimes it gave me multiple approaches so I could pick the one that fit my setup best. This was probably where I learned the most — not just copying the function but understanding the logic behind it so I could modify it when it did not work exactly as expected in my project.

### Example 1 — Debugging Genre Filter Showing 0 Movies

When I tapped the Adventure genre chip it showed 0 movies. The browser returned movies fine so I knew the backend was working. I pasted the Logcat output directly:

**Prompt:**
> Filter results for genre 'Adventure': 0 but in browser http://localhost:8001/movies/filter?genre=Adventure returns movies fine why

**Claude response:**
> Two possible reasons. First a stale-state bug — your onGenreSelected updates selectedGenre in state then immediately calls applyFilters() but the state update has not happened yet so it always filters with the previous genre. Fix this by passing the genre directly as a parameter to applyFilters instead of reading it from state.
> Second add error logging inside filterMovies() — the exception might be swallowing silently and returning empty list.

I added the logging myself and found this in Logcat:
FilterMovies error: java.lang.NumberFormatException: For input string: "1981è"

So I asked again with the exact error:

**Prompt:**
> some movies have year like 1981è and gson is crashing on it and returning empty list for whole page how to fix without losing other movies on that page

**Claude response:**
> Change year in MovieDto from Int? to String? then in MovieMapper strip everything that is not a digit before parsing:
> ```kotlin
> year = try {
>     val digits = this.year?.filter { it.isDigit() } ?: ""
>     if (digits.isEmpty()) 0 else digits.toInt()
> } catch (e: Exception) { 0 }
> ```
> Also register a custom Gson deserializer for Int in AppModule so corrupted numbers anywhere in the response fall back to 0 instead of crashing the whole page. Use mapNotNull when mapping the response list so one bad movie skips instead of crashing everything.

I implemented both fixes and tested each one separately on the emulator. After applying them Adventure showed 1,900 movies and every other genre that was previously showing 0 — Music, Musical, Short, Sport — all loaded correctly too.

---

### Example 2 — JWT Secret Mismatch Between Auth and Ratings Services

After setting up the reviews feature every POST to the reviews endpoint returned 401 Unauthorized even though login was working fine and the token was being sent.

**Prompt:**
> login works but posting review returns 401 jwt token is being sent in header what is wrong

**Claude response:**
> Most likely a JWT secret mismatch between your two services. The auth service signs the token with one secret key and the ratings service is trying to verify it with a different one. If the secrets do not match every token will be rejected as invalid even though it was legitimately issued.
> Check the JWT_SECRET environment variable in both services — they must be exactly the same string.

I checked both .env files and found the auth service was using `change-this-secret-key-at-least-32-bytes-long` and the ratings service had `supersecretkey123`. I coordinated with my teammate Farhan to align the secrets, rebuilt both Docker containers, and reviews started working immediately.

---

### Example 3 — ObjectId Crash in Reviews Service

After fixing the JWT secret, submitting a review crashed with a new error. I pasted it directly:

**Prompt:**
> bson.errors.InvalidId: rithik@gmail.com is not a valid ObjectId
> getting this when submitting review

**Claude response:**
> The reviews service is trying to convert the user_id into a MongoDB ObjectId using ObjectId(user["user_id"]) but user_id is an email string not a MongoDB ID. Email addresses are not valid ObjectIds.
> In your reviews.py change:
> ```python
> "user_id": ObjectId(user["user_id"])
> ```
> to:
> ```python
> "user_id": user["user_id"]
> ```
> Store the email as a plain string — this is actually better because you can compare it directly against the logged in user's email on the Android side when deciding whether to show the delete button.

I opened the file in Notepad, made the change, rebuilt the Docker container with --no-cache and confirmed reviews were saving and displaying correctly.

---

### Example 4 — Reviews Feature Architecture

Before writing any code for the reviews feature I wanted to understand how the delete button logic should work:

**Prompt:**
> i want reviews feature where user writes review with stars rating 1 to 5 and can delete but delete button should only show on users own reviews not on others how should this work

**Claude response:**
> You need three things working together. First when you fetch reviews from the API each review needs to carry the user_id of whoever wrote it. Second in your ReviewCard composable you compare that user_id against the currently logged in user's email — if they match show the delete button if not hide it. Third the delete endpoint on the backend needs a JWT token in the header so it can verify who is making the request.
> Your ReviewCard needs two extra parameters: the current user's email and an onDeleteClick callback. The delete button is just an if block:
> ```kotlin
> if (review.userId == currentUserEmail) {
>     IconButton(onClick = { onDeleteClick(review.id) }) { ... }
> }
> ```
> For the star rating display loop from 1 to 5 and show a filled star if the number is less than or equal to the review rating otherwise show an empty star.

I implemented the ReviewCard based on this explanation, wired it up to the DetailViewModel which fetches the current user's email after login, and tested that the delete button appeared only on my own reviews and not on others.

---

## 3. Reflection

I used Claude a lot in this project and want to be fully honest about that. The examples in section 2 are only a few of many conversations — there were hundreds of messages between me and Claude to get things working. The base architecture and all the screens I built myself using YouTube tutorials and Android documentation, but once I started connecting to the real backend services Claude became a constant tool I relied on.

The biggest advantage was debugging speed. Problems like the corrupted year crash and the JWT secret mismatch would have taken me much longer to trace alone. When I came with a specific Logcat error the answer was almost always useful. When I came with something vague the answer was vague too — I learned that the hard way.

The main limitation was that Claude does not know your specific project. It could tell me what kind of problem the JWT mismatch was but I still had to talk to my teammate Farhan to find the actual secret value his service was using. There were also moments where Claude suggested the same fix slightly differently without resolving the issue — that is when I learned to stop, read the error myself more carefully, and come back with something more specific. Every fix I received I tested on the emulator before committing.

---

## 4. AI-Assisted Sources in the Bibliography

No literature sources were found or suggested through AI tools in this submission. Claude was used exclusively for debugging, implementation decisions, and understanding concepts during development — not for research or source finding.

---

## 5. Bibliography

- Code With Harry. (n.d.). *Android Development Tutorials* [YouTube channel]. YouTube. https://www.youtube.com/@CodeWithHarry
- Android Developers. (n.d.). *Jetpack Compose documentation*. Google. https://developer.android.com/compose
- Square. (n.d.). *Retrofit — A type-safe HTTP client for Android and Java*. https://square.github.io/retrofit/
- Google. (n.d.). *Hilt dependency injection*. Android Developers. https://developer.android.com/training/dependency-injection/hilt-android