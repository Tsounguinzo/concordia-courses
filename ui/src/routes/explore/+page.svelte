<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {repo} from "$lib/repo";
    import FilterToggle from "$lib/components/common/filter/FilterToggle.svelte";
    import ExploreFilter from "$lib/components/explore/ExploreFilter.svelte";
    import SearchBar from "$lib/components/Search/SearchBar.svelte";
    import InfiniteScroll from 'svelte-infinite-scroll';
    import Spinner from "$lib/components/common/loader/Spinning.svelte";
    import JumpToTopButton from "$lib/components/common/JumpToTopButton.svelte";
    import CourseCard from "$lib/components/explore/CourseCard.svelte";
    import {allSortByOptions} from "$lib/types";
    import {darkModeOn} from "$lib/darkmode";
    import Skeleton from "$lib/components/common/loader/Skeleton.svelte";
    import {toast} from "svelte-sonner";
    import Seo from "$lib/components/common/Seo.svelte";
    import {faker} from '@faker-js/faker';
    import InstructorCard from "$lib/components/explore/InstructorCard.svelte";

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
    const coursesmock = [
        {"subject": "POLI", "catalog": "397"},
        {"subject": "SPEC", "catalog": "609"},
        {"subject": "RELI", "catalog": "685"},
        {"subject": "FTRA", "catalog": "615"},
        {"subject": "FPST", "catalog": "301"},
        {"subject": "PSYC", "catalog": "321"},
        {"subject": "IRST", "catalog": "209"},
        {"subject": "WILS", "catalog": "601"},
        {"subject": "BLDG", "catalog": "6701"},
        {"subject": "HIST", "catalog": "101"},
        {"subject": "MATH", "catalog": "205"},
        {"subject": "PHYS", "catalog": "284"},
        {"subject": "ECON", "catalog": "201"},
        {"subject": "BIO", "catalog": "205"},
        {"subject": "CHEM", "catalog": "206"},
        {"subject": "MANA", "catalog": "300"},
    ];

    const tags = [
        "Tough Grader",
        "Get Ready To Read",
        "Participation Matters",
        "Extra Credit",
        "Group Projects",
        "Amazing Lectures",
        "Clear Grading Criteria",
        "Gives Good Feedback",
        "Inspirational",
        "Lots Of Homework",
        "Hilarious",
        "Beware Of Pop Quizzes",
        "So Many Papers",
        "Caring",
        "Respected",
        "Flexible Deadlines",
        "Lecture Heavy",
        "Test Heavy",
        "Graded By Few Things",
        "Accessible Outside Class",
        "Online Savvy",
        "Engaging",
        "Technically Proficient",
        "Industry Experienced",
        "Research-Oriented",
        "Multidisciplinary Approach",
        "Interactive Sessions",
        "Encourages Critical Thinking",
        "Uses Multimedia",
        "Culturally Inclusive"
    ];

    function getRandomSubarray(arr, size) {
        const shuffled = arr.slice(0);
        let i = arr.length, temp, index;
        while (i--) {
            index = Math.floor((i + 1) * Math.random());
            temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled.slice(0, size);
    }

    function createFakeInstructor(id) {
        return {
            _id: id,
            firstName: faker.person.firstName(),
            lastName: faker.person.lastName(),
            department: faker.commerce.department(),
            courses: getRandomSubarray(coursesmock, Math.floor(Math.random() * 10)), // 0 to 10 courses
            tags: getRandomSubarray(tags, Math.floor(Math.random() * 3)), // 0 to 3 tags
            avgDifficulty: parseFloat((Math.random() * 5).toFixed(1)),
            avgRating: parseFloat((Math.random() * 5).toFixed(1)),
            reviewCount: Math.floor(Math.random() * 100)
        };
    }

    const fakeInstructors = [];
    for (let i = 0; i < 20; i++) {
        fakeInstructors.push(createFakeInstructor(faker.datatype.uuid()));
    }
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    type SortByType = (typeof allSortByOptions)[number];

    const makeSortPayload = (sort: SortByType) => {
        switch (sort) {
            case '':
                return null;
            case 'Best Experience':
                return {
                    sortType: 'experience',
                    reverse: true,
                };
            case 'Best Rating':
                return {
                    sortType: 'rating',
                    reverse: true,
                };
            case 'Worst Experience':
                return {
                    sortType: 'experience',
                    reverse: false,
                };
            case 'Worst Rating':
                return {
                    sortType: 'rating',
                    reverse: false,
                };
            case 'Hardest':
                return {
                    sortType: 'difficulty',
                    reverse: true,
                };
            case 'Easiest':
                return {
                    sortType: 'difficulty',
                    reverse: false,
                };
            case 'Most Reviews':
                return {
                    sortType: 'reviewCount',
                    reverse: true,
                };
            case 'Least Reviews':
                return {
                    sortType: 'reviewCount',
                    reverse: false,
                };
        }
    };

    const limit = 20;
    let courses: Course[];
    let hasMore = true;
    let offset = limit;
    let query = '';
    const searchSelected = writable<boolean>(false);
    const selectedLevels = writable<string[]>([]);
    const selectedSubjects = writable<string[]>([]);
    const selectedTerms = writable<string[]>([]);
    const selectedDepartments = writable<string[]>([]);
    const selectedTags = writable<string[]>([]);
    const selectedInstructors = writable<string[]>([]);
    const sortBy = writable<SortByType>('');
    let isMounted = false;
    let previousState = '';
    const instructorsModeOn = writable<boolean>(false);

    function toggle() {
        instructorsModeOn.update(state => !state);
    }

    const nullable = (arr: string[]) => (arr.length === 0 ? null : arr);

    $: filters = {
        subjects: nullable($selectedSubjects),
        levels: nullable($selectedLevels),
        departments: nullable($selectedDepartments),
        tags: nullable($selectedTags),
        instructors: nullable($selectedInstructors),
        terms: nullable($selectedTerms),
        query: query === '' ? null : query,
        sortBy: makeSortPayload($sortBy),
    };

    const fetchCourses = async (reset = false) => {
        try {
            const data = await repo.getCourses(limit, reset ? 0 : offset, filters);
            if (reset) {
                courses = data;
                offset = limit; // Reset offset if it's a fresh fetch
            } else {
                courses = [...courses, ...data];
                offset += limit;
            }
            hasMore = true;
        } catch (error) {
            toast.error('Failed to fetch courses. Please try again later.');
        }
    };

    onMount(() => {
        fetchCourses(true);
        isMounted = true;
    });

    $: {
        const currentState = JSON.stringify([$selectedSubjects, $selectedLevels, $selectedTerms, $selectedTags, $selectedInstructors, $selectedDepartments, $sortBy]);

        if (isMounted && (query !== '' || currentState !== previousState)) {
            fetchCourses(true);
            previousState = currentState;
        }
    }

    const fetchMore = async () => {
        const batch = await repo.getCourses(limit, offset, filters);

        if (!batch?.length) {
            hasMore = false;
        } else {
            courses = [...courses, ...batch];
            offset = offset + limit;
        }
    };
</script>
<Seo title="Explore" description="Explore courses and instructors at concordia.courses"/>
<div class='flex flex-col items-center py-8'>
    <h1 class='mb-16 text-center text-5xl font-bold tracking-tight text-gray-900 dark:text-gray-200 sm:text-5xl'>
        Explore all {$instructorsModeOn ? 'instructors' : 'courses'}
    </h1>
    <div class='relative flex w-full max-w-xl flex-col lg:max-w-6xl lg:flex-row lg:justify-center'>
        <div class='lg:hidden'>
            <FilterToggle>
                <ExploreFilter
                        {selectedSubjects}
                        {selectedLevels}
                        {selectedTerms}
                        {sortBy}
                        {selectedDepartments}
                        {selectedTags}
                        {selectedInstructors}
                        variant='mobile'
                        {instructorsModeOn}
                />
            </FilterToggle>
        </div>
        <div class='lg:flex-1'>
            <div class='ml-auto flex w-full max-w-xl flex-col overflow-y-hidden'>
                {#if courses !== undefined}
                    <SearchBar
                            handleInputChange={(value) => query = value}
                            iconStyle='mt-2 lg:mt-0 absolute top-1/4 transform -translate-y-1/4'
                            inputStyle='block rounded-lg w-full bg-slate-50 p-3 pr-5 pl-10 text-sm text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500'
                            outerInputStyle='my-2 mt-4 lg:mt-2 flex flex-col h-20 rounded-lg bg-slate-50 dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 rounded-lg'
                            placeholder={`Search by ${$instructorsModeOn ? "instructor's name, department or course code" : 'course code, title or description'}`}
                            searchSelected={searchSelected}
                    >
                        <button
                                on:click={toggle}
                                class="absolute bottom-4 right-0 mr-3 flex items-center"
                        >
                            <div class="relative flex ">
                                <div class="w-12 h-6 flex border border-gray-400 items-center rounded-full p-1 duration-300 ease-in-out"
                                     class:bg-blue-500={$instructorsModeOn}>
                                    <div class="dark:bg-white bg-neutral-400 w-4 h-4 rounded-full shadow-md transform duration-300 ease-in-out"
                                         class:translate-x-6={$instructorsModeOn}></div>
                                </div>
                                <span class="ml-2 font-semibold text-gray-600 dark:text-gray-400">Instructors</span>
                            </div>
                        </button>
                    </SearchBar>
                    {#if $instructorsModeOn}
                        {#each fakeInstructors as instructor}
                            <InstructorCard
                                    className='my-1.5'
                                    {instructor}
                                    {query}
                            />
                        {/each}
                    {:else}
                        {#each courses as course}
                            <CourseCard
                                    className='my-1.5'
                                    {course}
                                    {query}
                            />
                        {/each}
                    {/if}
                    <InfiniteScroll hasMore={hasMore} threshold={courses?.length || 20} window={true}
                                    on:loadMore={() => fetchMore()}/>
                {:else }
                    <div class='mx-2 text-gray-50'>
                        <Skeleton className='mb-2 rounded-lg first:mt-2'
                                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                    </div>
                {/if}

                {#if !hasMore || !courses?.length}
                    <div class='mx-auto mt-4 text-center'>
                        <p class='text-gray-500 dark:text-gray-400'>
                            Whoa! We've scrolled through them all. No more courses in sight!
                        </p>
                    </div>
                {:else }
                    <div class='mt-4 text-center'>
                        <Spinner/>
                    </div>
                {/if}
            </div>
        </div>
        <div class='m-2 mx-4 hidden lg:flex'>
            <ExploreFilter
                    selectedSubjects={selectedSubjects}
                    selectedLevels={selectedLevels}
                    selectedTerms={selectedTerms}
                    sortBy={sortBy}
                    variant='desktop'
                    {selectedDepartments}
                    {selectedTags}
                    {selectedInstructors}
                    {instructorsModeOn}
            />
        </div>
    </div>
</div>
<JumpToTopButton/>