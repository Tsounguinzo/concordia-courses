<script lang="ts">
    import CourseSearchBar from "$lib/components/Search/CourseSearchBar.svelte";
    import {getSearchIndex, updateSearchResults} from "$lib/searchIndex";
    import {searchResults} from "$lib/store";
    import Seo from "$lib/components/common/Seo.svelte";
    import Reviewcard from "$lib/components/common/animation/Reviewcard.svelte";
    import Marquee from "$lib/components/common/animation/Marquee.svelte";
    import DotPattern from "$lib/components/common/animation/DotPattern.svelte";

    const { courses, instructors, coursesIndex, instructorsIndex } = getSearchIndex();

    const handleInputChange = (query: string) => {
        updateSearchResults(
            query,
            courses,
            coursesIndex,
            instructors,
            instructorsIndex,
        );
    }

    // 15 Course Reviews
    const courseReviews = [
        { name: "ENGR243", username: "@Course", body: "Prof is clear and explains well.", img: "https://avatar.vercel.sh/ENGR243" },
        { name: "SOCI320", username: "@Course", body: "One of the best courses. Very engaging!", img: "https://avatar.vercel.sh/SOCI320" },
        { name: "BTM481", username: "@Course", body: "Time-consuming, but great project experience.", img: "https://avatar.vercel.sh/BTM481" },
        { name: "EDUC270", username: "@Course", body: "Useful content but slow lectures.", img: "https://avatar.vercel.sh/EDUC270" },
        { name: "MIAE313", username: "@Course", body: "Great introduction into machine parts.", img: "https://avatar.vercel.sh/MIAE313" }
    ];

    // 15 Instructor Reviews
    const instructorReviews = [
        { name: "Riccardo Romeo", username: "@Instructor", body: "Great prof, 100% recommend.", img: "https://avatar.vercel.sh/RiccardoRomeo" },
        { name: "Michel Deslauriers", username: "@Instructor", body: "Lectures are fun and interesting.", img: "https://avatar.vercel.sh/MichelDeslauriers" },
        { name: "Marcie Frank", username: "@Instructor", body: "Pop-up assignments, attend classes!", img: "https://avatar.vercel.sh/MarcieFrank" },
        { name: "Jesse Arseneault", username: "@Instructor", body: "Clear grading criteria and creative projects.", img: "https://avatar.vercel.sh/JesseArseneault" },
        { name: "Christine Jamieson", username: "@Instructor", body: "Easy online course, very manageable.", img: "https://avatar.vercel.sh/ChristineJamieson" }
    ];

</script>

<Seo title="Home" description="Home page of concordia.courses" />

<main class="relative flex flex-col w-full min-h-[calc(100vh-9vh)] items-center justify-center">

    <DotPattern
            class="[mask-image:radial-gradient(500px_circle_at_center,white,transparent)]"
    />

    <div class="relative z-10 flex flex-col gap-6 text-center">
        <h1 class="max-[320px]:text-3xl text-4xl  font-bold leading-snug text-gray-900 dark:text-gray-200 sm:text-5xl lg:text-6xl">
            7,832+ courses.<br/> Concordia Courses.
        </h1>
        <p class="text-base text-gray-600 dark:text-gray-400 sm:text-lg">
            Concordia Courses cumulates 61,727+ reviews for over 4,863 instructors.
        </p>
        <div class="relative w-full">
            <CourseSearchBar results={$searchResults} handleInputChange={handleInputChange} />
        </div>
    </div>

    <div class="relative flex h-full w-full flex-col items-center justify-center overflow-hidden py-4">
        <Marquee pauseOnHover class="[--duration:20s]">
            {#each instructorReviews as item}
                <Reviewcard {...item} />
            {/each}
        </Marquee>
        <Marquee reverse pauseOnHover class="[--duration:20s]">
            {#each courseReviews as item}
                <Reviewcard {...item} />
            {/each}
        </Marquee>
        <div class="pointer-events-none absolute inset-y-0 left-0 w-1/3 bg-gradient-to-r from-slate-200 dark:from-neutral-900"></div>
        <div class="pointer-events-none absolute inset-y-0 right-0 w-1/3 bg-gradient-to-l from-slate-200 dark:from-neutral-900"></div>
    </div>
</main>
