<script>
    let handleInputChange;
    let results;
</script>

<div class='relative isolate px-6 pt-14 lg:px-8'>
    <div class='mx-auto max-w-2xl py-8'>
        <div class='hidden sm:mb-8 sm:flex sm:justify-center'></div>
        <div class='text-center'>
            <h1 class='mb-6 py-3 text-left text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-200 md:text-5xl'>
                Explore thousands of course and professor reviews from McGill
                students
            </h1>
            <CourseSearchBar
                    results={results}
                    handleInputChange={handleInputChange}
            />
        </div>
    </div>
</div>