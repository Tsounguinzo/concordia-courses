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
    import {sortByOptions} from "$lib/types";
    import {darkModeOn} from "$lib/darkmode";
    import Skeleton from "$lib/components/common/loader/Skeleton.svelte";
    import {toast} from "svelte-sonner";
    import Seo from "$lib/components/common/Seo.svelte";

    type SortByType = (typeof sortByOptions)[number];

    const makeSortPayload = (sort: SortByType) => {
        switch (sort) {
            case '':
                return null;
            case 'Highest Rating':
                return {
                    sortType: 'rating',
                    reverse: true,
                };
            case 'Lowest Rating':
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
    const sortBy = writable<SortByType>('');
    let isMounted = false;
    let previousState = '';

    const nullable = (arr: string[]) => (arr.length === 0 ? null : arr);

    $: filters = {
        subjects: nullable($selectedSubjects),
        levels: nullable($selectedLevels),
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
        const currentState = JSON.stringify([$selectedSubjects, $selectedLevels, $selectedTerms, $sortBy]);

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
<Seo title="StudyHub | Explore" description="Explore courses at concordia.courses" />
<div class='flex flex-col items-center py-8'>
    <h1 class='mb-16 text-center text-5xl font-bold tracking-tight text-gray-900 dark:text-gray-200 sm:text-5xl'>
        Explore all courses
    </h1>
    <div class='relative flex w-full max-w-xl flex-col lg:max-w-6xl lg:flex-row lg:justify-center'>
        <div class='sm:mx-2 lg:hidden'>
            <FilterToggle>
                <ExploreFilter
                        {selectedSubjects}
                        {selectedLevels}
                        {selectedTerms}
                        {sortBy}
                        variant='mobile'
                />
            </FilterToggle>
        </div>
        <div class='lg:flex-1'>
            <div class='ml-auto flex w-full max-w-xl flex-col overflow-y-hidden'>
                {#if courses !== undefined}
                    <SearchBar
                            handleInputChange={(value) => query = value}
                            iconStyle='mt-2 lg:mt-0'
                            inputStyle='block rounded-lg w-full bg-slate-200 p-3 pr-5 pl-10 text-sm text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500'
                            outerInputStyle='my-2 mt-4 lg:mt-2'
                            placeholder='Search by course identifier, title or description'
                            searchSelected={searchSelected}
                    />
                    {#each courses as course}
                        <CourseCard
                                className='my-1.5'
                                {course}
                                {query}
                        />
                    {/each}
                    <InfiniteScroll hasMore={hasMore} threshold={courses?.length || 20} window={true}
                                    on:loadMore={() => fetchMore()}/>
                {:else }
                    <div class='mx-2 text-gray-50'>
                        <Skeleton className='mb-2 rounded-lg first:mt-2'
                                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                    </div>
                {/if}

                {#if !hasMore}
                    {#if courses?.length}
                        <div class='mx-auto mt-4 text-center'>
                            <p class='text-gray-500 dark:text-gray-400'>
                                No more courses to show
                            </p>
                        </div>
                    {:else }
                        <div class='mt-4 text-center'>
                            <Spinner/>
                        </div>
                    {/if}
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
            />
        </div>
    </div>
</div>
<JumpToTopButton/>