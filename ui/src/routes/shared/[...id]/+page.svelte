<script lang="ts">
    import Seo from "$lib/components/common/Seo.svelte";
    import BackgroundBeams from "$lib/components/common/animation/BackgroundBeams.svelte";
    import {page} from "$app/stores";
    import type {Review} from "$lib/model/Review";
    import {determineReviewFor} from "$lib/utils.js";
    import SharedReview from "$lib/components/review/Review.svelte";
    import {Info} from "lucide-svelte";

    const review: Review = $page.data.review;
    const error = $page.data.error;
    let reviewFor: string;

    if(!error) reviewFor = determineReviewFor(review)

</script>

<Seo title="Shared review" description="Shared review from concordia.courses" ogTitle="Shared review | Concordia.courses" ogDescription="Shared review from concordia.courses"/>

{#if error}
    <div class="flex w-full justify-center items-center min-h-[calc(100vh-22vh)]">
        <div class="flex items-center justify-center text-md font-medium text-gray-800 dark:text-gray-200">
            <div class="dark:bg-red-900 bg-red-400 rounded-md p-3 flex gap-x-4 items-center justify-center">
                <Info class="min-w-6 min-h-6"/>
                {error}
            </div>
        </div>
    </div>
{:else }
    <div class='flex w-full justify-center items-center min-h-[calc(100vh-22vh)]'>
        <div class='flex w-full justify-center mx-auto max-w-2xl'>
            <div class='mx-4 flex w-full flex-col rounded-md bg-slate-50 p-6 dark:bg-neutral-800 md:mt-10 relative'>
                <div class='flex flex-col items-center'>
                    <h1 class='text-3xl font-semibold text-center text-gray-800 dark:text-gray-200'>
                        Review for <strong>{reviewFor}</strong>
                    </h1>
                    <p class='text-gray-600 dark:text-gray-400 text-center'>
                        Shared Review
                    </p>
                </div>
                <div class="z-10">
                    <SharedReview
                            class="bg-transparent dark:bg-transparent"
                            canModify={false}
                            handleDelete={() => {}}
                            review={review}
                            interactions={[]}
                            updateLikes={undefined}
                            updateComments={undefined}
                    />
                </div>
                <BackgroundBeams/>
            </div>
        </div>
    </div>
{/if}