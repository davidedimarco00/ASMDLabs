# Workout Builder — Application Specification

## Overview

The Workout Builder is an application designed to help gym-goers plan, organize, and track their workout routines. A user can create and maintain multiple workout programs, each structured around a weekly schedule. During a training session, the user follows their plan step by step and records their progress as they go.

---

## Core Concepts

### Users and Workouts

Each user may own several **workouts**. A workout represents a complete training program — for example, "12-Week Strength Program" or "Summer Cut Routine." The user can switch between workouts freely and keep historical ones for reference.

### Weeks and Days

A workout is divided into one or more **weeks**. Each week contains one or more **training days**, and each day has a human-readable name (e.g., "Push Day," "Leg Day," "Active Recovery"). The structure can vary from week to week — for instance, Week 1 might have four training days while Week 3 might have five, and the names and contents of those days can differ across weeks.

### Steps (The Building Blocks of a Day)

Each training day is composed of an ordered list of **steps**. A step is a single activity the user performs during their session. There are three types of steps:

#### 1. Cardio Step

A cardio step represents a cardiovascular exercise — running on a treadmill, cycling, rowing, etc. Its primary parameter is **duration** (how long the user should perform the activity). Optionally, the step can include intensity notes such as target heart rate or speed.

#### 2. Warm-Up Step

A warm-up step groups together one or more light preparatory exercises, each defined by a **time duration**. The purpose is to prepare the body before heavier work. For example, a warm-up step might include "2 minutes of arm circles" followed by "1 minute of hip openers." Each individual exercise within the warm-up is time-based rather than repetition-based.

#### 3. Weight Lifting Step

A weight lifting step is the most structured type. It represents a single resistance exercise (e.g., Bench Press, Squat, Deadlift) and can be configured in one of three formats:

- **Max effort** — The user performs as many repetitions as possible in a single set. The application records *how many* reps the user completed. Example: "Bench Press — max reps."

- **Sets × Reps at Weight** — The classic gym notation. The user performs a fixed number of sets, each with a fixed number of repetitions, at a specified weight. For example, "3 × 10 at 60 kg" means three sets of ten repetitions using 60 kilograms. This is the most common format.

- **Time-based** — The user holds or performs the exercise for a set duration rather than counting reps. Example: "Plank — 45 seconds" or "Farmer's Walk — 1 minute at 30 kg."

---

## Exercise Descriptions

Every exercise referenced in a step should have an associated **description**. This description serves two purposes:

1. **What the exercise is** — A plain-language explanation of the movement (e.g., "A compound pushing movement targeting chest, shoulders, and triceps").

2. **How to perform it** — A step-by-step breakdown of proper form. This should be detailed enough that a user unfamiliar with the exercise can follow along safely. For example:

   > *Barbell Bench Press:*
   > 1. Lie flat on the bench with your eyes under the bar.
   > 2. Grip the bar slightly wider than shoulder width.
   > 3. Unrack the bar and lower it to your mid-chest.
   > 4. Press the bar back up until your arms are fully extended.
   > 5. Keep your feet flat on the floor and your shoulder blades squeezed together throughout.

Descriptions are tied to the exercise itself, not to a specific step. This means if "Squat" appears in multiple days or weeks, they all share the same description.

---

## Progress Tracking

While the user is performing a training session, they should be able to **update their progress in real time**. This means:

- For **cardio** and **time-based** steps, the user can mark the step as completed and optionally note the actual duration or any adjustments made.
- For **warm-up** steps, the user can check off each individual warm-up exercise as they finish it.
- For **weight lifting (Sets × Reps)** steps, the user can log each set individually — recording the actual weight used and the actual number of reps completed (which may differ from the plan).
- For **max effort** steps, the user records the number of reps achieved.

Progress data should be persisted so the user can review past sessions and track improvement over time.

