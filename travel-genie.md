# Travel Genie - Firebase-First Travel Planning Platform

Travel Genie is a modern travel planner built with **React + TypeScript + Vite** on the client and **Firebase** (Auth, Firestore, Cloud Functions, Hosting) for backend services. There is no separate FastAPI/MySQL server—everything persists in Firestore or runs in Cloud Functions.

## Architecture
User Browser  
      ↓  
React SPA (Vite) → Firebase Auth (email/password) → Firestore (data) → Cloud Functions (server logic) → Hosting (SPA)

## Data Model (Firestore)
- `users/{uid}` → `email`, `fullName`, `username`, `avatarUrl`, `location`, `bio`, `isPremium`, timestamps
- `destinations/{destinationId}` → destination metadata (name, city, country, category, imageUrl, costs, etc.)
- `travels/{travelId}` → `userId`, `destinationId`, `title`, `description`, `startDate`, `endDate`, `status`, `budget`, `currency`, `numberOfTravelers`, `isPublic`, `imageUrl`, `notes`, `shareToken`, timestamps
  - `itineraryItems/{itemId}` → per-day/slot itinerary entries
  - `tripPlans/{tripPlanId}` → plan/budget for a travel
    - `plannedActivities/{activityId}` → planned activities for a trip plan
  - `sharedAccess/{shareId}` → optional sharing access metadata
- `reviews/{reviewId}` → trip/destination reviews
- `achievements/{achievementId}` and `users/{uid}/achievements/{achievementId}` → catalog + per-user progress

## Auth
- Email/password only via Firebase Auth (`createUserWithEmailAndPassword`, `signInWithEmailAndPassword`).
- On signup, a profile document is created at `users/{uid}` with `isPremium=false` and server timestamps.
- The client loads the profile doc into the auth context for dashboard/profile UI.

## Cloud Functions
- HTTPS endpoints live in `functions/src/index.ts`.
- Example: `getRates?base=USD` fetches live FX, caches in Firestore with TTL, and is consumed by the frontend (no localhost endpoints).

## Hosting & Routing
- `npm run build` outputs to `dist/`; Firebase Hosting serves it with SPA rewrites to `index.html`.
- All data calls go directly to Firestore/Auth or to Functions HTTPS endpoints.

## Environment
- `.env.local` uses only `VITE_FIREBASE_*` keys (see `.env.firebase.example`).
- No `VITE_API_BASE_URL` or SQL credentials are used.

## Development & Deploy
- `npm install`
- `npm run dev` (uses your Firebase project/emulator)
- `npm run build`
- `firebase deploy` (Hosting + Firestore rules + indexes + Functions)
