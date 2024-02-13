<script lang="ts">
    import {getCurrentTerms} from "$lib/utils";
    import type {Course} from "$lib/model/Course";
    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import FilterToggle from "$lib/components/Explore/FilterToggle.svelte";
    import ExploreFilter from "$lib/components/Explore/ExploreFilter.svelte";
    import SearchBar from "$lib/components/Search/SearchBar.svelte";
    import InfiniteScroll from 'svelte-infinite-scroll';
    import Spinner from "$lib/components/Spinner.svelte";
    import JumpToTopButton from "$lib/components/Explore/JumpToTopButton.svelte";
    import CourseCard from "$lib/components/Explore/CourseCard.svelte";
    import {sortByOptions} from "$lib/types";
    import data from '$lib/data/test.json'

    type SortByType = (typeof sortByOptions)[number];

    const makeSortPayload = (sort: SortByType) => {
        switch (sort) {
            case '':
                return undefined;
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
    const currentTerms = getCurrentTerms();

    const courses = writable<Course[] | undefined>(undefined);
    const hasMore = writable(true);
    const offset = writable(limit);

    const query = writable<string>('');
    const searchSelected = writable<boolean>(false);
    const selectedLevels = writable<string[]>([]);
    const selectedSubjects = writable<string[]>([]);
    const selectedTerms = writable<string[]>([]);
    const sortBy = writable<SortByType>('');

    const nullable = (arr: string[]) => (arr.length === 0 ? null : arr);

    const filters = {
        subjects: nullable($selectedSubjects),
        levels: nullable($selectedLevels.map((l) => l.charAt(0))),
        terms: nullable(
            $selectedTerms.map(
                (term) => currentTerms.filter((t) => t.split(' ')[0] === term)[0]
            )
        ),
        query: $query === '' ? null : query,
        sortBy: makeSortPayload($sortBy),
    };

  /*  $: if ($selectedSubjects || $selectedLevels || $selectedTerms || $sortBy || $query) {
        repo
            .getCourses(limit, 0, filters) // Adjust according to how filters are defined or used
            .then((data) => {
                courses.set(data);
                hasMore.set(true); // Assuming this logic is correct for your case
                offset.set(limit); // Update offset based on limit
            })
            .catch(() => {
                toast.error('Failed to fetch courses. Please try again later.');
            });
    } */

    onMount(() => {
        /*repo
            .getCourses(limit, 0, filters)
            .then((data) => courses.set(data))
            .catch(() => {
                toast.error('Failed to fetch courses. Please try again later.');
            });*/
        courses.set(data) //to remove
        hasMore.set(true);
        offset.set(limit);
    });

    const fetchMore = async () => {
        const batch = await repo.getCourses(limit, $offset, filters);

        if (batch.length === 0) hasMore.set(false);
        else {
            courses.set($courses?.concat(batch));
            offset.set($offset + limit);
        }
    };
</script>

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
                {#if $courses}
                    <SearchBar
                            handleInputChange={(value) => query.set(value)}
                            iconStyle='mt-2 lg:mt-0'
                            inputStyle='block rounded-lg w-full bg-slate-200 p-3 pr-5 pl-10 text-sm text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500'
                            outerInputStyle='my-2 mt-4 lg:mt-2'
                            placeholder='Search by course identifier, title, description or instructor name'
                            searchSelected={searchSelected}
                    />
                    {#each $courses as course, i (i)}
                        <CourseCard
                                className='my-1.5'
                                course={course}
                                query={$query}
                        />
                    {/each}
                {:else }
                    <div class='mx-2 text-gray-50'>
                        <!--Skeleton
                                baseColor={
                        $darkModeOn ? 'rgb(38 38 38)' : 'rgb(248 250 252)'
                      }
                                class='mb-2 rounded-lg first:mt-2'
                                count={10}
                                duration={2}
                                height={256}
                                highlightColor={
                        $darkModeOn ? 'rgb(64 64 64)' : 'rgb(226 232 240)'
                      }
                        /-->
                        THISSSSSSSSSSSSSS
                    </div>
                {/if}

                {#if !$hasMore}
                    {#if $courses?.length}
                        <div class='mx-[200px] mt-4 text-center'>
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
            <InfiniteScroll threshold={$courses?.length || 0} on:loadMore={fetchMore}/>
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