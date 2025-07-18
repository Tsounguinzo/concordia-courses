<script lang="ts">
    import Seo from "$lib/components/common/Seo.svelte";
    import JumpToTopButton from "$lib/components/common/JumpToTopButton.svelte";
    import InfiniteScroll from "svelte-infinite-scroll";
    import Skeleton from "$lib/components/common/loader/Skeleton.svelte";
    import Spinner from "$lib/components/common/loader/Spinning.svelte";
    import {feedSortByOptions} from "$lib/types";
    import {get, writable} from "svelte/store";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import {onMount} from "svelte";
    import FeedFilter from "$lib/components/common/filter/FeedFilter.svelte";
    import type {Review} from "$lib/model/Review";
    import InstructorOrCourseReview from "$lib/components/review/Review.svelte";
    import {type Interaction} from "$lib/model/Interaction";
    import {page} from "$app/stores";
    import {spliceCourseCode} from "$lib/utils";
    import {instructorIdToName} from "$lib/utils.js";
    import {visitorId} from "$lib/store";
    import {mode} from "mode-watcher";
    import type {Comment} from "$lib/model/Comment";

    type SortByType = (typeof feedSortByOptions)[number];

    const makeSortPayload = (sort: SortByType) => {
        switch (sort) {
            case '':
                return null;
            case 'Best':
                  return 'experience';
            case 'Hot':
                 return 'likes';
            case 'New':
                return 'date';
            default:
                return null;
        }
    };

    const user = $page.data.user;
    const limit = 20;
    let reviews: Review[];
    let hasMore = true;
    let offset = limit;
    const sortBy = writable<SortByType>('New');
    let isMounted = false;
    let previousState = '';
    const userInteractions = writable<Interaction[] | undefined>([]);

    $: filters = {
        sortBy: makeSortPayload($sortBy),
    };

    const fetchCourses = async (reset = false) => {
        try {
            const data = await repo.getReviewsFeed(limit, reset ? 0 : offset, filters);
            if (reset) {
                reviews = data;
                offset = limit; // Reset offset if it's a fresh fetch
            } else {
                reviews = [...reviews, ...data];
                offset += limit;
            }
            hasMore = true;
        } catch (error) {
            toast.error('Failed to fetch courses. Please try again later.');
        }
    };

    onMount(async () => {
        await fetchCourses(true);
        isMounted = true;
        const userId = user?.id ?? $visitorId;
        if (userId) {
            const userInteractionsPayload =
                await repo.getUserInteractions(userId);

            userInteractions.set(userInteractionsPayload);
        }
    });

    $: {
        const currentState = JSON.stringify($sortBy);

        if (isMounted && (currentState !== previousState)) {
            fetchCourses(true);
            previousState = currentState;
        }
    }

    const fetchMore = async () => {
        const batch = await repo.getReviewsFeed(limit, offset, filters);

        if (!batch?.length) {
            hasMore = false;
        } else {
            reviews = [...reviews, ...batch];
            offset = offset + limit;
        }
    };

    const updateLikes = (review: Review) => {
        return (likes: number) => {
            if (reviews) {
                const updated = reviews.slice();
                const r = updated.find(
                    (r) => r.courseId == review.courseId && r.userId == review.userId
                );

                if (r === undefined) {
                    toast.error("Can't update likes for review that doesn't exist.");
                    return;
                }

                r.likes = likes;
                reviews = updated;
            }
        };
    };

    const updateComments = (review: Review) => {
        return (comments: Comment[]) => {
            if (reviews) {
                const updated = reviews.slice();
                const found = updated.find((r) => r._id === review._id);
                if (!found) {
                    toast.error("Can't update comments for review that doesn't exist.");
                    return;
                }
                found.comments = comments;
                reviews = updated;
            }
        };
    };
</script>

<Seo title="Reviews Feed" description="Reviews feed for courses at concordia.courses" ogTitle="Reviews Feed | Concordia.courses" ogDescription="Reviews feed for courses at concordia.courses" ogImageAlt="concordia.courses Reviews page Snapshot" ogImage="og-image-feed.png"/>
<div class='flex flex-col items-center py-8'>
    <h1 class='mb-16 text-center text-5xl font-bold tracking-tight text-gray-900 dark:text-gray-200 sm:text-5xl'>
        What Students Think
    </h1>
    <div class='relative flex w-full max-w-xl flex-col lg:max-w-6xl lg:flex-row lg:justify-center'>
        <div class='m-auto flex w-full max-w-xl flex-col overflow-y-hidden'>
            {#if reviews !== undefined}
                <FeedFilter {sortBy}/>
                {#each reviews as review (review._id)}
                    <InstructorOrCourseReview
                            title={review?.type === 'instructor' ? instructorIdToName(review?.instructorId) : spliceCourseCode(review?.courseId, " ")}
                            interactions={$userInteractions}
                            updateLikes={updateLikes(review)}
                            updateComments={updateComments(review)}
                            class='rounded-md my-1.5'
                            review={review}
                            canModify={false}
                            handleDelete={() => undefined}
                            editReview={writable(false)}
                    />
                {/each}
                <InfiniteScroll hasMore={hasMore} threshold={reviews?.length || 20} window={true}
                                on:loadMore={() => fetchMore()}/>
            {:else }
                <div class='mx-2 text-gray-50'>
                    <Skeleton class='mb-2 rounded-lg first:mt-2'
                              color={$mode === 'dark' ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                </div>
            {/if}

            {#if !hasMore || !reviews?.length}
                <div class='mx-auto mt-4 text-center'>
                    <p class='text-gray-500 dark:text-gray-400'>
                        Whoa! We've scrolled through them all. No more reviews in sight!
                    </p>
                </div>
            {:else }
                <div class='mt-4 text-center'>
                    <Spinner/>
                </div>
            {/if}
        </div>
    </div>
</div>
<JumpToTopButton/>