# luleme · 撸了么

Android health tracking app for aviation professionals. Log daily health metrics, track trends over time, and get AI-powered insights on your physical condition and readiness.

> Fork of [sky22333/luleme](https://github.com/sky22333/luleme), extended with AI health analysis features.

---

## Features

- **Daily health logging** — record sleep, exercise, and key vitals
- **Timeline & calendar views** — visualize health trends over days, weeks, and months
- **AI health analysis** *(in progress)* — LLM-powered insights on patterns, anomalies, and personalized recommendations based on your logged data
- **Offline-first** — all data stored locally, no account required
- **Material Design** — clean Android-native UI

## Tech Stack

| | |
|--|--|
| **Language** | Kotlin · Java |
| **UI** | Android SDK · Material Design |
| **Storage** | Room Database |
| **AI** | Claude API / OpenAI API *(planned)* |

## AI Analysis *(Roadmap)*

Planned AI features:
- **Pattern detection** — identify correlations between sleep, exercise, and performance metrics
- **Personalized recommendations** — actionable suggestions based on your health history
- **Anomaly alerts** — flag unusual readings that may need attention
- **Natural language summaries** — weekly/monthly health report in plain language

Implementation plan: lightweight FastAPI backend endpoint that receives anonymized health snapshots and returns Claude API analysis.

## Build

```bash
git clone https://github.com/pintaste/luleme.git
# Open in Android Studio
# Build > Make Project
```

Minimum SDK: Android 8.0 (API 26)

## License

MIT
