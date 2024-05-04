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
    import InstructorCard from "$lib/components/explore/InstructorCard.svelte";
    import type {Instructor} from "$lib/model/Instructor";
    import {createFakeInstructor} from "$lib/mockData";

    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    const fakeInstructors = [];
    for (let i = 0; i < 20; i++) {
        fakeInstructors.push(createFakeInstructor());
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
    let instructors: Instructor[];
    let hasMoreCourses = true;
    let hasMoreInstructors = true;
    let offset = limit;
    let query = '';
    const searchSelected = writable<boolean>(false);
    const selectedLevels = writable<string[]>([]);
    const selectedSubjects = writable<string[]>([]);
    const selectedTerms = writable<string[]>([]);
    const selectedDepartments = writable<string[]>([]);
    const selectedTags = writable<string[]>([]);
    const sortBy = writable<SortByType>('');
    let isMounted = false;
    let previousState = '';
    const instructorsModeOn = writable<boolean>(false);

    function toggle() {
        instructorsModeOn.update(state => !state);
        fetchData(true);
    }

    const nullable = (arr: string[]) => (arr.length === 0 ? null : arr);

    $: filters = {
        subjects: nullable($selectedSubjects),
        levels: nullable($selectedLevels),
        departments: nullable($selectedDepartments),
        tags: nullable($selectedTags),
        terms: nullable($selectedTerms),
        query: query === '' ? null : query,
        sortBy: makeSortPayload($sortBy),
    };

    const fetchData = async (reset = false) => {
        try {
            let data;
            if ($instructorsModeOn) {
                data = await repo.getInstructors(limit, reset ? 0 : offset, filters);
                if (reset) instructors = data;
                else instructors = [...instructors, ...data];
                hasMoreInstructors = data?.length === limit;
            } else {
                data = await repo.getCourses(limit, reset ? 0 : offset, filters);
                if (reset) courses = data;
                else courses = [...courses, ...data];
                hasMoreCourses = data?.length === limit;
            }
            offset = reset ? limit : offset + limit;
        } catch (error) {
            console.log(error)
            toast.error(`Failed to fetch ${$instructorsModeOn ? 'instructors' : 'courses'}. Please try again later.`);
        }
    };

    onMount(() => {
        fetchData(true);
        isMounted = true;
    });

    $: {
        const currentState = JSON.stringify([$selectedSubjects, $selectedLevels, $selectedTerms, $selectedTags, $selectedDepartments, $sortBy]);

        if (isMounted && (query !== '' || currentState !== previousState)) {
            fetchData(true);
            previousState = currentState;
        }
    }

    const fetchMore = async () => {
        try {
            let batch;
            if ($instructorsModeOn) {
                batch = await repo.getInstructors(limit, offset, filters);
                if (batch?.length) {
                    instructors = [...instructors, ...batch];
                }
                hasMoreInstructors = batch?.length === limit;
            } else {
                batch = await repo.getCourses(limit, offset, filters);
                if (batch?.length) {
                    courses = [...courses, ...batch];
                }
                hasMoreCourses = batch?.length === limit;
            }

            offset = offset + limit;
        } catch (error) {
            toast.error(`Failed to fetch more ${$instructorsModeOn ? 'instructors' : 'courses'}. Please try again later.`);
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
                        variant='mobile'
                        {instructorsModeOn}
                />
            </FilterToggle>
        </div>
        <div class='lg:flex-1'>
            <div class='ml-auto flex w-full max-w-xl flex-col overflow-y-hidden'>
                    <SearchBar
                            handleInputChange={(value) => query = value}
                            iconStyle='mt-2 lg:mt-0 absolute top-1/3 transform -translate-y-1/2'
                            inputStyle='block rounded-lg w-full bg-slate-50 p-3 pr-5 pl-10 text-md text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500'
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
                {#if (!$instructorsModeOn && courses !== undefined) || ($instructorsModeOn && fakeInstructors !== undefined) }
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
                    <InfiniteScroll hasMore={$instructorsModeOn ? hasMoreInstructors : hasMoreCourses}
                                    threshold={($instructorsModeOn ? instructors?.length : courses?.length) || 20}
                                    window={true}
                                    on:loadMore={() => fetchMore()}/>
                {:else }
                    <div class='mx-2 text-gray-50'>
                        <Skeleton className='mb-2 rounded-lg first:mt-2'
                                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                    </div>
                {/if}

                {#if (!$instructorsModeOn && !hasMoreCourses) || ($instructorsModeOn && !hasMoreInstructors)}
                    <div class='mx-auto mt-4 text-center'>
                        <p class='text-gray-500 dark:text-gray-400'>
                            Whoa! We've scrolled through them all. No more {$instructorsModeOn ? 'instructors' : 'courses'} in sight!
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
                    {instructorsModeOn}
            />
        </div>
    </div>
</div>
<JumpToTopButton/>