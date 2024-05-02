<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import Transition from "svelte-transition";
    import {ChevronDown} from "lucide-svelte";
    import {writable} from "svelte/store";
    import {createCombobox} from "svelte-headlessui";

    export let options: readonly string[];
    export let name: string;
    export let reset: boolean = false;
    export let value: string;
    export let setFieldValue: (name: string, value: any) => void;

    const selection = writable<string>(value);

    let query = writable('');

    $: filtered = $query !== '' ?
        options.filter((x) => {
            return x.toLowerCase().includes($query.toLowerCase());
        })
        : options;

    $: if(reset) {
        combobox.set({ selected: ''});
    }

    $: selection.set($combobox.selected)
    const combobox = createCombobox({label: 'Actions', selected: value ?? ''});

    selection.subscribe((currentSelection) => {
        setFieldValue(name, currentSelection);
    });

    query.subscribe((currentQuery) => {
        if ($query !== '') combobox.set({ selected: currentQuery });
    });
</script>


    <div>
        <div class='w-full'>
            <div class='relative'>
                <input use:combobox.input class={$$props.class}
                       on:input={(event) => query.set(event.target?.value)}
                       value={$combobox.selected}
                       on:blur={(event) => event.target.value = $combobox.selected}
                       placeholder={$$props.placeholder}
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
                <div class="relative">
                <ul use:combobox.items
                    class='autocomplete absolute w-full max-h-80 overflow-scroll rounded-md text-sm shadow-md'>
                    {#each filtered as value}
                        {@const active = $combobox.active === value}
                        <li use:combobox.item={{ value }}
                            class={twMerge('cursor-pointer p-2 text-gray-900 dark:text-gray-200 min-h-[32px]',
                            active ? 'bg-gray-100 dark:bg-neutral-500' : 'bg-white dark:bg-neutral-600')}>
                            {value}
                        </li>
                    {/each}
                </ul>
                </div>
            </Transition>
        </div>
    </div>
