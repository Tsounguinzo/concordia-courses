<script lang="ts">
    import type {CourseInstructor} from "$lib/model/Instructor";
    import {StarIcon} from 'lucide-svelte';
    import {instructorNameToUrlParam} from "$lib/utils";

    export let instructors: CourseInstructor[];

    // Sort instructors by rating and review count for ranking
    $: sortedInstructors = [...instructors].sort((a, b) => {
        const scoreA = a.avgRating * Math.log10(a.reviewCount + 1);
        const scoreB = b.avgRating * Math.log10(b.reviewCount + 1);
        return scoreB - scoreA;
    });
</script>

{#key instructors}
    {#if instructors.length}
        <div class="space-y-2 p-4 relative w-full rounded-md bg-slate-50 shadow-sm dark:bg-neutral-800">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-base font-medium text-gray-900 dark:text-gray-100">Instructor Rankings</h2>
                <span class="text-sm text-gray-500">{instructors.length} Total</span>
            </div>

            <!-- Scrollable container -->
            <div class="max-h-[600px] overflow-y-auto pr-2 space-y-2 scrollbar-thin scrollbar-thumb-primary-500 scrollbar-track-gray-100">
                {#each sortedInstructors as instructor, i}
                    <button class="flex items-center gap-3 p-3 bg-gray-50 dark:bg-neutral-800 rounded border-l-4
                {i === 0 ? 'border-primary-600' :
                 i === 1 ? 'border-primary-500' :
                 i === 2 ? 'border-primary-400' : 'border-gray-200'}
                hover:bg-gray-100 dark:hover:bg-neutral-700 transition-colors duration-200"
                            on:click={() => window.location.href = `/instructor/${instructorNameToUrlParam(instructor._id)}`}
                            on:mousedown={() => window.location.href = `/instructor/${instructorNameToUrlParam(instructor._id)}`}
                    >

                        <!-- Rank -->
                        <div class="w-8 text-center font-mono text-sm text-gray-500">
                            #{i + 1}
                        </div>

                        <!-- Main Content -->
                        <div class="flex-grow grid grid-cols-[1fr,auto] gap-4 items-center">
                            <!-- Instructor Info -->
                            <div>
                                <h3 class="text-left font-medium text-gray-900 dark:text-gray-100">
                                    {instructor.firstName} {instructor.lastName}
                                </h3>

                                <div class="flex items-center gap-3 mt-1">
                                    <!-- Rating Stars -->
                                    <div class="flex items-center gap-1">
                                        <div class="flex">
                                            {#each Array(5) as _, index}
                                                <StarIcon
                                                        size={12}
                                                        class={index < Math.round(instructor.avgRating) ?
                                                'text-primary-500 fill-current' :
                                                'text-gray-300'}
                                                />
                                            {/each}
                                        </div>
                                        <span class="text-sm text-gray-600 dark:text-gray-400">
                                    {instructor.avgRating.toFixed(1)}
                                </span>
                                    </div>

                                    <!-- Difficulty -->
                                    <div class="flex items-center gap-1">
                                        <span class="text-sm text-gray-500">Difficulty:</span>
                                        <div class="h-1.5 w-16 bg-gray-200 rounded-full overflow-hidden">
                                            <div
                                                    class="h-full bg-primary-600 opacity-75"
                                                    style="width: {(instructor.avgDifficulty / 5) * 100}%"
                                            ></div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Review Count -->
                            <div class="text-right">
                        <span class="text-sm text-gray-500">
                            {instructor.reviewCount} reviews
                        </span>
                            </div>
                        </div>
                    </button>
                {/each}
            </div>
        </div>
    {/if}
{/key}