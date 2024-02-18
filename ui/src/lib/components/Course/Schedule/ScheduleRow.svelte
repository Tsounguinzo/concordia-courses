<script lang="ts">
    import TimeblockDays from "$lib/components/Course/Schedule/TimeblockDays.svelte";
    import BlockLocation from "$lib/components/Course/Schedule/BlockLocation.svelte";
    import type {Block} from "$lib/model/Schedule";

    export let block: Block

    const timeToDisplay = (time: string) => {
        const [hour, minute] = time.split('.', 2)

        return `${hour.toLocaleString()}:${minute.toLocaleString()}`;
    };
</script>

<tr class='p-2 text-left even:bg-slate-100 even:dark:bg-[rgb(48,48,48)]'>
    <td class='whitespace-nowrap pl-4 text-sm font-semibold sm:pl-6 sm:text-base'>
        {`${block.componentCode} ${block.section}`}
    </td>
    <td class='py-2 text-gray-700 dark:text-gray-300'>
        <div class='flex flex-col items-start pl-1 text-center font-medium'>
            <BlockLocation roomCode={block.roomCode} buildingCode={block.buildingCode}/>
        </div>
    </td>
    <td class='whitespace-nowrap py-2 text-sm font-medium sm:text-base'>
        <div>
            {timeToDisplay(block.classStartTime)} - {timeToDisplay(block.classEndTime)}
        </div>
    </td>
    <td class='p-2'>
        <TimeblockDays {block}/>
    </td>
</tr>