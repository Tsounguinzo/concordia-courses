<script lang="ts">
    import {getSearchIndex, updateSearchResults} from "$lib/searchIndex";
    import {writable} from "svelte/store";
    import {mobileMenuOpen, searchResults} from "$lib/store";
    import CourseSearchBar from "$lib/components/Search/CourseSearchBar.svelte";
    import {page} from "$app/stores";
    import DarkModeToggle from "./DarkModeToggle.svelte";
    import {Menu} from "lucide-svelte";
    import SideNav from "./SideNav.svelte";
    import ProfileDropdown from "$lib/components/profile/ProfileDropdown.svelte";
    import NotificationDropdown from "$lib/components/profile/NotificationDropdown.svelte";
    import {onMount} from "svelte";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";

    const {courses, instructors, coursesIndex, instructorsIndex} = getSearchIndex();

    const user = null; //useAuth();
    $: pathName = $page.url.pathname;
    const notifications = writable<Notification[]>([]);

    onMount(() => {
        if (user) {
            repo
                .getNotifications()
                .then((data) => notifications.set(data))
                .catch(() => toast.error('Failed to get notifications.'));
        }
    });

    const handleInputChange = (query: string) => {
        updateSearchResults(
            query,
            courses,
            instructors,
            coursesIndex,
            instructorsIndex,
        );
    };

    const reset = () => {
        searchResults.set({
            query: '',
            courses: [],
            instructors: [],
        });
    };

</script>

<header class='z-40'>
    <nav
            class='z-40 flex items-center justify-between p-3 lg:px-8'
            aria-label='Global'
    >
        <div class='z-40 my-auto mr-auto flex min-w-[48px] lg:flex-1'>
            <a href='/' class='-m-1.5 p-1.5'>
                <img class='h-12 w-auto' src='/studyhub.png' alt='Study Hub'/>
            </a>
        </div>
        {#if pathName !== '/'}
            <div class='mx-8 my-auto hidden flex-1 justify-center align-middle sm:mx-12 sm:block md:mx-28'>
                <CourseSearchBar results={$searchResults} {handleInputChange} onResultClick={reset}/>
            </div>
        {/if}
        {#if user}
            <div class='mr-2 lg:hidden'>
                <NotificationDropdown {notifications}/>
            </div>
        {/if}
        <div class='flex lg:hidden'>
            <button
                    type='button'
                    class='inline-flex items-center justify-center rounded-md p-2.5 text-gray-700 dark:text-gray-200'
                    on:click={() => mobileMenuOpen.set(true)}
            >
                <span class='sr-only'>Open main menu</span>
                <Menu
                        class='h-6 w-6 stroke-2 text-gray-400'
                        aria-hidden='true'
                />
            </button>
        </div>
        <div class='flex min-w-fit flex-row lg:flex-1'>
            <div class='my-auto hidden gap-x-1 lg:ml-auto lg:flex lg:items-center'>
                <DarkModeToggle/>
                {#if user}
                    <NotificationDropdown {notifications}/>
                {/if}
            </div>
            <div class='hidden lg:ml-4 lg:flex lg:justify-end'>
                {#if $user}
                    <ProfileDropdown/>
                {:else}

                    <a href="/login"
                       class='rounded-md bg-slate-50 px-3 py-2 text-sm font-semibold text-gray-500 hover:bg-gray-100 dark:bg-neutral-800 dark:text-gray-200 dark:hover:bg-neutral-700'>
                        Log in
                    </a>
                {/if}
            </div>
        </div>
    </nav>
    <SideNav/>
</header>