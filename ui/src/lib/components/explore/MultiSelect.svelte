<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import {Check, ChevronDown, X} from "lucide-svelte";
    import Transition from "svelte-transition";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {createCombobox} from "svelte-headlessui";

    export let options: string[];
    export let values: Writable<string[]>;
    export let reset: boolean = false;
    export let inputClassName: string = '';

    const query = writable('');

    // Needed to prevent the onBlur (resetting query) from firing when just clicking an option
    const optionClicked = writable(false);

    $: filtered = $query === '' ? options
        : options.filter((x) => {
            return x.toLowerCase().includes($query.toLowerCase());
        });

    $: values.set($combobox.selected)

    $: if(reset) {
        combobox.set({ selected: [] });
    }

    const handleInputBlur = () => {
        setTimeout(() => {
            if (!$optionClicked) query.set('');
            optionClicked.set(false);
        }, 200);
    };

    const handleOptionMouseDown = () => {
        optionClicked.set(true);
    };

    const handleOptionMouseUp = () => {
        optionClicked.set(false);
    };

    let combobox = createCombobox({label: 'multiselect', selected: []})
</script>
<div class={$$props.class}>
    <div>
        <div class='w-full'>
            <div class='relative max-w-[240px] rounded-md border bg-slate-200 p-2 dark:border-neutral-600 dark:bg-neutral-700'>
                <input placeholder="Select..."
                        use:combobox.input class={twMerge('w-[87.5%] bg-slate-200 text-sm outline-none dark:bg-neutral-700 dark:text-gray-200 dark:caret-white',
                inputClassName
              )}
                       on:input={(event) => query.set(event.target?.value)}
                       on:blur={handleInputBlur}
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
            >
                <ul use:combobox.items
                    class='scrollbar-hide autocomplete absolute max-h-80 w-full max-w-[240px] overflow-scroll rounded-md text-sm shadow-md'>
                    {#each filtered as value, i (i)}
                        {@const active = $combobox.active === value}
                        {@const selected = $combobox.selected.includes(value)}
                        <li use:combobox.item={{ value }}
                            class={twMerge('cursor-pointer p-2 text-gray-900 dark:text-gray-200 min-h-[32px]',
                            active ? 'bg-gray-100 dark:bg-neutral-500' : 'bg-white dark:bg-neutral-600')}
                            on:mousedown={handleOptionMouseDown}
                            on:mouseup={handleOptionMouseUp}
                        >

                            <div class='flex items-center justify-between'>
                                <div>{value}</div>
                                {#if selected}
                                    <Check class='stroke-blue-600' size={18}/>
                                {/if}
                            </div>
                        </li>
                    {/each}
                </ul>
            </Transition>
            <div class='mt-2 flex w-full flex-wrap gap-1'>
                    {#each $combobox.selected as selected (selected)}
                        <div class='max-w-60 flex space-x-1 rounded-3xl bg-gray-100 px-2.5 py-1 text-sm font-medium text-gray-800 dark:bg-neutral-700 dark:text-gray-200'>
                            <div class="text-xs">{selected}</div>
                            <button type='button' use:combobox.deselect={selected}>
                                <X size={18} class='transition duration-75 hover:stroke-blue-600'/>
                            </button>
                        </div>
                    {/each}
            </div>
        </div>
    </div>
</div>