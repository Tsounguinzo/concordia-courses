<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import {Check, ChevronDown, X} from "lucide-svelte";
    import Transition from "svelte-transition";
    import {writable} from "svelte/store";
    import type {Writable} from "svelte/store";
    import {createCombobox} from "svelte-headlessui";

    export let options: string[];
    export let values: Writable<string[]>;
    export let className: string = '';
    export let inputClassName: string = '';

    const query = writable('');

    // Needed to prevent the onBlur (resetting query) from firing when just clicking an option
    const optionClicked = writable(false);

    const filtered =
        $query === ''
            ? options
            : options.filter((x) => {
                return x.toLowerCase().includes($query.toLowerCase());
            });

    const removeVal = (val: string) => {
        values.set($values.filter((x) => x !== val));
    };

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

    const combobox = createCombobox({label: 'multiselect', selected: []})
</script>

<div class={twMerge('w-full', className)}>
    <div
            value={$values}
            on:change={(val) => {
          values.set(val);
        }}
    >
        <div class='relative max-w-[240px]'>
            <div class={twMerge( 'relative rounded-md bg-slate-200 p-2 dark:bg-neutral-700 border dark:border-neutral-600',
              inputClassName
            )}
            >
                <input use:combobox.button
                       class={twMerge(
                'w-full bg-slate-200 text-sm outline-none dark:bg-neutral-700 dark:text-gray-200 dark:caret-white',
                inputClassName
              )}
                       on:change={(event) => query.set(event.target?.value)}
                       on:blur={handleInputBlur}
                />
                <button use:combobox.button class='absolute inset-y-0 flex w-full items-center'>
                    <ChevronDown className='ml-auto mr-4 h-5 w-5 text-gray-400'
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
                    class='autocomplete absolute max-h-80 w-full overflow-scroll rounded-b-md text-sm shadow-md'>
                    {#each filtered as value, i (i)}
                        {@const active = $combobox.active === value}
                        <li use:combobox.item={{ value }}
                            value={value}
                            class={
                 twMerge(
                      'cursor-pointer p-2 text-gray-900 dark:text-gray-200',
                      active
                        ? 'bg-gray-100 dark:bg-neutral-500'
                        : 'bg-white dark:bg-neutral-600'
                    )
                  }
                            on:mousedown={handleOptionMouseDown}
                            on:mouseup={handleOptionMouseUp}
                        >
                            <div class='flex items-center justify-between'>
                                <div>{value}</div>
                                {#if $values.includes(value)}
                                    <Check className='stroke-red-600' size={18}/>
                                {/if}

                            </div>
                        </li>
                    {/each}
                </ul>
            </Transition>
        </div>
    </div>
    <div class='mt-2 flex w-full flex-wrap gap-1'>
        {#each $values as val, i (i)}
            <div class='flex space-x-1 rounded-3xl bg-gray-100 px-2.5 py-1 text-sm font-medium text-gray-800 dark:bg-neutral-700 dark:text-gray-200'>
                <div>{val}</div>
                <button type='button' on:click={() => removeVal(val)}>
                    <X size={18} class='transition duration-75 hover:stroke-red-600'/>
                </button>
            </div>
        {/each}
    </div>
</div>