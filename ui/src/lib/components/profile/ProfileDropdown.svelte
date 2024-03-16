<script>
    import {User} from "lucide-svelte";
    import Transition from "svelte-transition";
    import {createMenu} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {onMount} from "svelte";
    import {handleLogout} from "$lib";

    let groups = []

    onMount(() => {
        groups = [
            {text: 'Profile', url: `/profile`, action: null },
            {text: 'Log out', url: "", action: handleLogout },
        ]
    })
    const menu = createMenu({label: 'Actions'})
</script>

<div class='relative inline-block text-left'>
    <div>
        <button use:menu.button
                class='rounded-md bg-slate-50 px-3 py-2 text-sm font-semibold text-gray-500 hover:bg-gray-100 dark:bg-neutral-800 dark:text-gray-200 dark:hover:bg-neutral-700'>
            <User class='h-5 w-5 dark:text-gray-400' aria-hidden='true'/>
        </button>
    </div>
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
             class='absolute right-0 z-10 mt-2 w-56 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black/5 focus:outline-none dark:bg-neutral-800 dark:text-gray-200'>
            <div class='py-1 flex justify-center'>
                {#each groups as option}
                    {@const active = $menu.active === option.text}
                    <button use:menu.item>
                        <a href={option.url}
                           on:click={option.action}
                           class={twMerge(
                                active
                                    ? 'bg-gray-100 text-gray-900 dark:bg-neutral-700 dark:text-gray-200'
                                    : 'text-gray-700 dark:bg-neutral-800 dark:text-gray-200',
                                option.text === 'Profile'
                                ? 'block px-4 py-2 text-sm'
                                : 'block w-full px-4 py-2 text-left text-sm'
                            )}
                        >
                            {option.text}
                        </a>
                    </button>
                {/each}

            </div>
        </div>
    </Transition>
</div>