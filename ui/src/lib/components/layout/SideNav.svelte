<script lang="ts">
    import {onMount} from "svelte";
    import {page} from "$app/stores";
    import Transition from 'svelte-transition'
    import DarkModeToggle from "./DarkModeToggle.svelte";
    import {X} from "lucide-svelte";
    import {navigationItems} from "$lib/constants";
    import {mobileMenuOpen} from "$lib/store";
    import {handleLogout} from "$lib";

    const user = $page.data.user;

    onMount(() => {
        document.body.style.overflow = $mobileMenuOpen ? 'hidden' : 'auto';
    });
</script>

{#if $mobileMenuOpen}
        <div class='fixed h-screen inset-0 z-50 flex items-end justify-end'
        >
            <button class='fixed inset-0' on:click|preventDefault={() => mobileMenuOpen.set(false)}>
                    <div class='absolute inset-0 bg-black opacity-60'></div>
            </button>

            <Transition
                    show={true}
                    appear
                    enter='transform ease-in-out transition duration-500 sm:duration-300'
                    enterFrom='translate-x-full'
                    enterTo='translate-x-0'
                    leave='transform ease-in-out transition duration-300'
                    leaveFrom='translate-x-0'
                    leaveTo='translate-x-full'
            >
                <div class='z-10 h-screen w-full overflow-y-auto bg-white p-6 dark:bg-neutral-800 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10'>
                    <div class='mt-1 flex items-center justify-between'>
                        <div class='flex items-center'>
                            <a href='/'>
                                <img
                                        width="32"
                                        height="32"
                                        src="/logo.webp"
                                        alt='Concordia courses logo'
                                        on:click|preventDefault={() => mobileMenuOpen.set(false)}
                                />
                            </a>
                            <div class='ml-6'>
                                <DarkModeToggle/>
                            </div>
                        </div>
                        <button
                                type='button'
                                class='mr-2 rounded-2xl p-1 text-gray-700 hover:bg-gray-50 dark:text-gray-200 dark:hover:bg-neutral-700'
                                on:click|preventDefault={() => mobileMenuOpen.set(false)}
                        >
                            <span class='sr-only'>Close menu</span>
                            <X className='h-6 w-6' aria-hidden='true'/>
                        </button>
                    </div>
                    <div class='mt-6 flow-root'>
                        <div class='-my-6 divide-y divide-gray-500/10'>
                            <div class='space-y-2 py-6'>
                                {#each navigationItems as item}
                                    <a href={item.href}
                                       class='-mx-3 block rounded-lg px-3 py-2 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50 dark:text-gray-200 dark:hover:bg-neutral-700'
                                       on:click={() => mobileMenuOpen.set(false)}
                                    >
                                        {item.name}
                                    </a>
                                {/each}
                            </div>
                            <div class='py-6'>
                                {#if user}
                                    <div>
                                        <a href='/profile'
                                           class='-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50 dark:text-gray-200 dark:hover:bg-neutral-700'
                                        >
                                            Profile
                                        </a>
                                        <a href=""
                                           on:click={handleLogout}
                                           on:click={() => mobileMenuOpen.set(false)}
                                           class='-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50 dark:text-gray-200  dark:hover:bg-neutral-700'
                                        >
                                            Log out
                                        </a>
                                    </div>
                                {:else }
                                    <a href='/login'
                                            class='-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50 dark:text-gray-200  dark:hover:bg-neutral-700'
                                       on:click={() => mobileMenuOpen.set(false)}
                                    >
                                        Log in
                                    </a>
                                {/if}
                            </div>
                        </div>
                    </div>
                </div>
            </Transition>
        </div>
{/if}