<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import Transition from "svelte-transition";
    import {ChevronDown} from "lucide-svelte";
    import {type Writable, writable} from "svelte/store";
    import {createCombobox} from "svelte-headlessui";

    export let options: readonly T[];
    export let storeValue: Writable<T>;
    export let className: string = '';
    export let inputClassName: string = '';

    let query = writable('');

    $: filtered = $query !== '' ?
        options.filter((x) => {
            return x.toLowerCase().includes($query.toLowerCase());
        })
        : options;

    $: storeValue.set($combobox.selected)
    const combobox = createCombobox({label: 'Actions', selected: options[0]})
</script>

<div class={className}>
    <div>
        <div class='w-full'>
            <div class='relative max-w-[240px] rounded-md border bg-slate-200 p-2 dark:border-neutral-600 dark:bg-neutral-700'>
                <input use:combobox.input class={twMerge('w-[87.5%] bg-slate-200 text-sm outline-none dark:bg-neutral-700 dark:text-gray-200 dark:caret-white', inputClassName)}
                       on:input={(event) => query.set(event.target?.value)}
                       value={$combobox.selected}
                       on:blur={(event) => event.target.value = $combobox.selected}
                       placeholder="Select..."
                />
                <button use:combobox.button class='absolute inset-y-0 flex w-full items-center'>
                    <ChevronDown class='ml-auto mr-4 h-5 w-5 text-gray-400'
                                 aria-hidden='true'
                    />
                </button>
            </div>
            <Transition
                    show={$combobox.expanded}
                    enter='transition duration-100 ease-out'
                    enterFrom='transform scale-95 opacity-0'
                    enterTo='transform scale-100 opacity-100'
                    leave='transition duration-75 ease-out'
                    leaveFrom='transform scale-100 opacity-100'
                    leaveTo='transform scale-95 opacity-0'
                    on:after-leave={() => combobox.reset()}
            >
                <ul use:combobox.items
                    class='autocomplete absolute max-h-80 w-full max-w-[240px] overflow-scroll rounded-md text-sm shadow-md'>
                    {#each filtered as value}
                        {@const active = $combobox.active === value}
                        <li use:combobox.item={{ value }}
                            class={twMerge('cursor-pointer p-2 text-gray-900 dark:text-gray-200 min-h-[32px]',
                            active ? 'bg-gray-100 dark:bg-neutral-500' : 'bg-white dark:bg-neutral-600')}>
                            {value}
                        </li>
                    {/each}
                </ul>
            </Transition>
        </div>
    </div>
</div>