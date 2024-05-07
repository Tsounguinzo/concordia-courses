<script lang="ts">
    import type {Writable} from "svelte/store";
    import Transition from "svelte-transition";
    import type {Notification} from "$lib/model/Notification";
    import {Bell, Dot, Trash} from "lucide-svelte";
    import CourseReview from "$lib/components/review/Review.svelte";
    import {courseIdToUrlParam, spliceCourseCode} from "$lib/utils";
    import {createMenu} from "svelte-headlessui";
    import {toast} from "svelte-sonner";
    import {repo} from "$lib/repo";
    import {observeNotification} from "$lib";
    import {writable} from "svelte/store";

    export let notifications: Writable<Notification[]>;

    let seen: Set<string> = new Set();

    $: if ($menu.expanded && seen.size) {

        notifications.set(
            $notifications.map((n) => {
                return seen.has(`${n.review.userId} ${n.review.courseId}`) ? {...n, seen: true} : n;
            })
        );

        seen = new Set();
    }

    const updateNotification = async (notification: Notification) => {
        if (notification.seen) return;

        try {
            await repo.updateNotification(
                notification.review.courseId,
                notification.review.userId,
                true
            );
            seen.add(`${notification.review.userId} ${notification.review.courseId}`);
        } catch (err) {
            toast.error('Failed to update notification.');
        }
    };

    const deleteNotification = async (courseId: string, creatorId: string) => {
        try {
            await repo.deleteNotification(courseId, creatorId);
            notifications.set(
                $notifications.filter(
                    (notification) => notification.review.courseId !== courseId && notification.review.userId !== creatorId
                )
            );
            toast.success('Successfully deleted notification.');
        } catch (err) {
            toast.error('Failed to delete notification.');
        }
    };

    const menu = createMenu({label: 'Actions'})
</script>

<div class='z-50 text-right'>
    <div class='relative inline-block text-left rounded-2xl hover:bg-gray-50 dark:hover:bg-neutral-700'>
        <button use:menu.button
                class='m-2 inline-flex justify-center text-sm font-medium text-white focus:outline-none focus-visible:ring-2 focus-visible:ring-white'>
            <div class='relative'>
                <Bell class='h-5 w-5 text-neutral-500 dark:text-gray-400'
                      aria-hidden='true'
                />
                {#if $notifications.filter((notification) => !notification.seen).length !== 0}
                    <div class='absolute right-[3px] top-[1px] h-2 w-2 rounded-full bg-red-600'/>
                {/if}
            </div>
        </button>
        <Transition
                show={$menu.expanded}
                enter='transition ease-out duration-100'
                enterFrom='transform opacity-0 scale-95'
                enterTo='transform opacity-100 scale-100'
                leave='transition ease-in duration-75'
                leaveFrom='transform opacity-100 scale-100'
                leaveTo='transform opacity-0 scale-95'
        >
            <div use:menu.items
                 class=' absolute -right-8 z-20 mt-2 max-h-[800px] max-w-[325px] origin-top-right divide-y divide-gray-100 overflow-auto rounded-md bg-slate-100 shadow-lg dark:bg-neutral-700 md:max-w-[800px]'>
                <div class='p-2'>
                    {#if $notifications.length !== 0}
                        {#each $notifications as notification, i (i)}
                            <div use:observeNotification={{ updateNotification, notification }} use:menu.item
                                 class='m-2'>
                                <div class='mb-2 flex items-center dark:bg-neutral-700'>
                                    <div class='flex items-center gap-x-1'>
                                        <a class='font-semibold text-gray-800 dark:text-gray-200 dark:hover:text-blue-400 hover:text-blue-400'
                                           href={`/course/${courseIdToUrlParam(notification.review.courseId)}`}>
                                            {spliceCourseCode(notification.review.courseId, ' ')}
                                        </a>
                                        {#if !notification.seen}
                                            <Dot class='text-blue-400'/>
                                        {/if}
                                    </div>
                                    <button class='ml-auto text-right text-gray-700 hover:text-red-600 dark:text-gray-300 dark:hover:text-red-600'
                                            on:click|stopPropagation={async () =>
                                                        await deleteNotification(
                                                            notification.review.courseId,
                                                            notification.review.userId
                                                        )
                                                    }>
                                        <Trash/>
                                    </button>
                                </div>
                                <CourseReview
                                        className='rounded-md'
                                        review={notification.review}
                                        canModify={false}
                                        handleDelete={() => undefined}
                                        editReview={writable(false)}
                                        shareable={false}
                                />
                            </div>
                        {/each}
                    {:else }
                        <p class='w-[325px] p-1 text-sm font-medium leading-6 text-gray-600 dark:text-gray-300'>
                            All caught up! Subscribe to courses to get notified when
                            a user leaves a review.
                        </p>
                    {/if}
                </div>
            </div>
        </Transition>
    </div>
</div>