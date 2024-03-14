<script lang="ts">
    import { cn } from '$lib/utils';
    import { Motion } from 'svelte-motion';

    type LoadingState = {
        text: string;
    };

    export let loadingStates: LoadingState[];
    export let value: number = 0;
</script>

<div class="relative mx-auto mt-40 flex max-w-xl flex-col justify-start">
    {#each loadingStates as loadingState, index (index)}
        {@const distance = Math.abs(index - value)}
        <!-- Minimum opacity is 0, keep it 0.2 if you're sane. -->
        {@const opacity = Math.max(1 - distance * 0.2, 0)}

        <Motion
                let:motion
                initial={{ opacity: 0, y: -(value * 40) }}
                animate={{ opacity: opacity, y: -(value * 40) }}
                transition={{ duration: 0.5 }}
        >
            <div use:motion class={cn('mb-4 flex gap-2 text-left')}>
                <div>
                    {#if index > value}
                        <svg
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke-width={1.5}
                                stroke="currentColor"
                                class={cn('h-6 w-6 text-black dark:text-white')}
                        >
                            <path d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
                        </svg>
                    {/if}

                    {#if index <= value}
                        <svg
                                xmlns="http://www.w3.org/2000/svg"
                                viewBox="0 0 24 24"
                                fill="currentColor"
                                class={cn(
								'h-6 w-6 ',
								cn(
									'text-black dark:text-white',
									value === index && 'text-black opacity-100 dark:text-blue-500'
								)
							)}
                        >
                            <path
                                    fill-rule="evenodd"
                                    d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z"
                                    clip-rule="evenodd"
                            />
                        </svg>
                    {/if}
                </div>
                <span
                        class={cn(
						'text-black dark:text-white',
						value === index && 'text-black opacity-100 dark:text-blue-500'
					)}
                >
					{loadingState.text}
				</span>
            </div>
        </Motion>
    {/each}
</div>
